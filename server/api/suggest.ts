import type { VercelRequest, VercelResponse } from "@vercel/node";
import OpenAI from "openai";

const MAX_DESCRIPTION_LENGTH = 300;
const MODEL = "gpt-4o-mini";

/**
 * POST /api/suggest
 * Body:     { "description": string }
 * Response: { "activityName": string, "itemNames": string[] }
 *
 * Este endpoint es el ÚNICO lugar que conoce la API key de OpenAI. La app de
 * Wear OS nunca la ve — solo le habla a este proxy (ver
 * app/src/main/java/com/didiforget/ai/RemoteAIService.kt en el proyecto Android).
 *
 * El header `x-app-secret` no es un secreto perfecto: vive embebido en el APK
 * igual que viviría la key de OpenAI si se la hubiera puesto ahí directamente.
 * Pero cumple un propósito real: evita que un escaneo automatizado que
 * encuentre esta URL la use como API gratis, y si alguna vez se filtra, es
 * gratis rotarlo (una variable de entorno) — a diferencia de una key de
 * OpenAI ya facturada.
 */
export default async function handler(req: VercelRequest, res: VercelResponse) {
  if (req.method !== "POST") {
    res.setHeader("Allow", "POST");
    return res.status(405).json({ error: "method_not_allowed" });
  }

  const expectedSecret = process.env.APP_SHARED_SECRET;
  if (expectedSecret && req.headers["x-app-secret"] !== expectedSecret) {
    return res.status(401).json({ error: "unauthorized" });
  }

  const description = typeof req.body?.description === "string" ? req.body.description.trim() : "";
  if (!description) {
    return res.status(400).json({ error: "empty_description" });
  }
  if (description.length > MAX_DESCRIPTION_LENGTH) {
    return res.status(400).json({ error: "description_too_long" });
  }

  const apiKey = process.env.OPENAI_API_KEY;
  if (!apiKey) {
    console.error("OPENAI_API_KEY no está configurada en las variables de entorno de Vercel.");
    return res.status(500).json({ error: "server_misconfigured" });
  }

  const client = new OpenAI({ apiKey });

  try {
    const completion = await client.chat.completions.create({
      model: MODEL,
      max_tokens: 300,
      temperature: 0.4,
      response_format: { type: "json_object" },
      messages: [
        {
          role: "system",
          content:
            "Eres el asistente de la app 'Did I Forget?'. A partir de la descripción de una " +
            "actividad, devuelves un nombre corto para la actividad y una lista de objetos " +
            "físicos que la persona debería llevar. Responde SOLO con JSON con esta forma " +
            'exacta: {"activityName": string, "itemNames": string[]}. ' +
            "itemNames debe tener entre 3 y 8 elementos, cada uno un objeto concreto y breve " +
            "(ej. 'Cargador', 'Documento de identificación'), en español, sin numerarlos ni " +
            "agregar texto fuera del JSON."
        },
        { role: "user", content: description }
      ]
    });

    const raw = completion.choices[0]?.message?.content;
    if (!raw) throw new Error("empty_completion");

    const parsed = JSON.parse(raw) as { activityName?: unknown; itemNames?: unknown };

    const activityName =
      typeof parsed.activityName === "string" && parsed.activityName.trim().length > 0
        ? parsed.activityName.trim()
        : description.charAt(0).toUpperCase() + description.slice(1);

    const itemNames = Array.isArray(parsed.itemNames)
      ? parsed.itemNames.filter(
          (item): item is string => typeof item === "string" && item.trim().length > 0
        )
      : [];

    if (itemNames.length === 0) {
      return res.status(502).json({ error: "ai_bad_response" });
    }

    return res.status(200).json({ activityName, itemNames });
  } catch (error) {
    console.error("Fallo llamando a OpenAI:", error);
    return res.status(502).json({ error: "ai_unavailable" });
  }
}
