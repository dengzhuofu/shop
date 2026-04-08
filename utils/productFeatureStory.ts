export interface FeatureMetric {
  label: string
  value: string
}

export interface FeaturePanel {
  id: string
  title: string
  body: string
  image?: string
  metrics?: FeatureMetric[]
  size?: 'wide' | 'tall' | 'standard'
  tone?: 'dark' | 'light' | 'accent'
}

export interface FeatureStory {
  eyebrow: string
  title: string
  subtitle: string
  heroImage: string
  heroMetrics: FeatureMetric[]
  panels: FeaturePanel[]
}

const sNovaFeatureStory: FeatureStory = {
  eyebrow: 'Features',
  title: 'Private-mold commuter design with a more expressive night ride.',
  subtitle:
    'The S Nova leans into comfort, visibility, and approachable city performance with a tighter commuter footprint than the larger performance models.',
  heroImage:
    'https://cdn.shopify.com/s/files/1/0588/3592/7218/files/1_c3cd5297-1587-4df5-ab05-5a41dc4fa928.png?v=1763109319',
  heroMetrics: [
    { label: 'Max Range', value: '19 Miles' },
    { label: 'Max Power', value: '600W' },
    { label: 'Max Speed', value: '20 MPH' },
    { label: 'Slope', value: '15%' },
  ],
  panels: [
    {
      id: 's-nova-feature-grid',
      title: 'Feature collage from the live product story.',
      body:
        'The real iSinwheel page uses a large visual montage to sell comfort, braking, certification, app control, and the lighting setup in one glance.',
      image:
        'https://cdn.shopify.com/s/files/1/0588/3592/7218/files/f61bfa06bcb545c497bf596234326667.png?v=1768211223',
      size: 'wide',
      tone: 'dark',
    },
    {
      id: 's-nova-suspension',
      title: 'Dual suspension for cracked pavement and campus shortcuts.',
      body:
        'The S Nova is aimed at everyday city use, so the suspension and 8.5-inch pneumatic tires are positioned as comfort upgrades instead of race-focused hardware.',
      image:
        'https://www.isinwheel.com/cdn/shop/files/8b9cc7c9808a81fc8db0eaf67a4d79d7_e490986b-ebf2-47fb-b7f1-81aa7cc3aee2.jpg?v=1764156934&width=1600',
      size: 'tall',
      tone: 'light',
    },
    {
      id: 's-nova-lighting',
      title: 'Ambient deck lights and turn signals add visibility after dark.',
      body:
        'This model is more lifestyle-forward than the utilitarian commuter scooters in the catalog, which makes it a strong visual fit for a richer detail page.',
      image:
        'https://www.isinwheel.com/cdn/shop/files/86d6b6668dd249b94188acb64812e4bf.jpg?v=1764156934&width=1600',
      tone: 'accent',
      metrics: [
        { label: 'Load', value: '264 LBS' },
        { label: 'Tires', value: '8.5 Inch' },
      ],
    },
    {
      id: 's-nova-app',
      title: 'Smart app support for lock status, ride data, and light controls.',
      body:
        'The app-connected story helps the lower half of the page feel like a premium product detail experience instead of a plain spec dump.',
      image:
        'https://cdn.shopify.com/s/files/1/0588/3592/7218/files/20bc95811e70462bbfbd089f841c03fb.png?v=1764069889&width=800',
      tone: 'light',
    },
  ],
}

export const buildProductFeatureStory = (
  product: any,
  galleryImages: string[] = [],
): FeatureStory | null => {
  if (!product) {
    return null
  }

  if (product.slug === 's-nova-commuting-electric-scooter') {
    return sNovaFeatureStory
  }

  const specs = Array.isArray(product.specs) ? product.specs : []
  const quickKnow = Array.isArray(product.quickKnow) ? product.quickKnow : []
  const safeImages = galleryImages.filter(Boolean)
  const fallbackImage = safeImages[0] || product.pic || ''

  return {
    eyebrow: 'Features',
    title: `${product.title} is tuned for a clearer, more visual product story.`,
    subtitle:
      product.subtitle ||
      'Below the hero section, we continue the product page with spec-driven panels and richer image treatment.',
    heroImage: fallbackImage,
    heroMetrics: specs.slice(0, 4).map((item: any) => ({
      label: String(item?.label || 'Spec'),
      value: String(item?.value || ''),
    })),
    panels: [
      {
        id: 'generic-story-hero',
        title: quickKnow[0] || 'Built to feel stronger than a plain product summary.',
        body:
          quickKnow[1] ||
          'This section uses the product gallery and spec system to create a more visual long-scroll experience.',
        image: safeImages[1] || fallbackImage,
        size: 'wide',
        tone: 'dark',
      },
      {
        id: 'generic-story-detail',
        title: quickKnow[2] || 'Specification-led storytelling.',
        body:
          quickKnow[3] ||
          'Key selling points are pulled forward so shoppers do not need to rely on the accordion alone.',
        image: safeImages[2] || fallbackImage,
        tone: 'light',
      },
    ],
  }
}
