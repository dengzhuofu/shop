export interface FeatureMetric { label: string; value: string }
export interface FeaturePanel { id: string; title: string; body: string; image: string; size?: 'wide'|'tall'; tone?: 'dark'|'light'|'accent'; metrics?: FeatureMetric[] }
export interface FeatureStory { eyebrow: string; title: string; subtitle: string; heroImage: string; heroMetrics: FeatureMetric[]; panels: FeaturePanel[] }
export const buildProductFeatureStory = (product:any, galleryImages:string[] = []): FeatureStory | null => {
  if (!product) return null
  const zh = /[\u3400-\u9fff]/.test(product.title || '')
  const points = Array.isArray(product.quickKnow) ? product.quickKnow : []
  const images = galleryImages.filter(Boolean)
  return {
    eyebrow: zh ? '商品特点' : 'Product details', title: product.title, subtitle: product.subtitle || '',
    heroImage: images[0] || product.pic || '',
    heroMetrics: (product.specs || []).slice(0,4).map((x:any) => ({ label: String(x.label || ''), value: String(x.value || '') })),
    panels: [
      { id:'details', title: points[0] || (zh ? '查看商品细节' : 'Explore the details'), body: points[1] || product.subtitle || '', image: `/cbjj/products/${product.id}/detail.png`, size:'wide', tone:'light' },
      { id:'everyday', title: points[2] || (zh ? '找到适合自己的选择' : 'Find your everyday fit'), body: points[3] || (zh ? '请核对所选变体、参数及使用说明，骑行时遵守当地规定。' : 'Review the selected variant, specifications and manual, and follow local rules.'), image:`/cbjj/products/${product.id}/scene.png`, tone:'light' },
    ].filter(panel => images.includes(panel.image)),
  }
}
