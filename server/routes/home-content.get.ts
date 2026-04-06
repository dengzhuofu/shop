import { promises as fs } from 'node:fs'
import path from 'node:path'

export default defineEventHandler(async () => {
  const filePath = path.resolve(process.cwd(), 'data', 'isinwheel-home.json')

  try {
    const raw = await fs.readFile(filePath, 'utf-8')
    return JSON.parse(raw)
  } catch (error) {
    return {
      source: 'fallback',
      heroSlides: [],
      featureVideo: {},
      customerReviews: [],
      blogCards: [],
    }
  }
})
