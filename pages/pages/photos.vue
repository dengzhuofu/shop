<template>
  <div class="photos-page">
    <section class="container photos-shell">
      <div class="photos-masonry">
        <figure v-for="photo in photos" :key="photo" class="photo-card">
          <img :src="photo" alt="isinwheel rider photo" loading="lazy" />
        </figure>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
useSeoMeta({
  title: 'Photos',
  description:
    'Explore a masonry gallery of rider moments, product shots, and community photos from isinwheel.',
})

const photos = [
  'https://www.isinwheel.com/cdn/shop/files/0F5703CE-6AEC-4fa7-AF66-AF5EC93C62A8.png?v=1703754465',
  'https://www.isinwheel.com/cdn/shop/files/1_26328d49-2686-4e5f-a93c-9d66959bd368.jpg?v=1703817962',
  'https://www.isinwheel.com/cdn/shop/files/1000X1000_300ba21c-0ba2-4e48-9685-42dc1246793a.jpg?v=1703747596',
  'https://www.isinwheel.com/cdn/shop/files/2_4aadf927-a549-4543-90d4-187754bb9bc3.jpg?v=1703818026',
  'https://www.isinwheel.com/cdn/shop/files/20bc95811e70462bbfbd089f841c03fb.png?v=1764069889',
  'https://www.isinwheel.com/cdn/shop/files/385525584_347206937710606_109972174088588481_n.jpg?v=1703754576',
  'https://www.isinwheel.com/cdn/shop/files/387878746_1927134881020361_2819692373100417123_n_370d2e8b-6368-4e5c-99fc-77ba09ebae6c.jpg?v=1704522976',
  'https://www.isinwheel.com/cdn/shop/files/401677935_3605732819697619_6249656458202610064_n.jpg?v=1703754645',
  'https://www.isinwheel.com/cdn/shop/files/404032783_3610040492600185_4834536195061545707_n_1.jpg?v=1703754699',
  'https://www.isinwheel.com/cdn/shop/files/404978589_3611971635740404_4077119476198805463_n.jpg?v=1703754739',
  'https://www.isinwheel.com/cdn/shop/files/c62a4e87259303bdd1f3ffda340e92ba.jpg?v=1703754720',
  'https://www.isinwheel.com/cdn/shop/files/DSC03695.jpg?v=1703818027',
  'https://www.isinwheel.com/cdn/shop/files/DSC03718.jpg?v=1703817962',
  'https://www.isinwheel.com/cdn/shop/files/IMG_0918.jpg?v=1703817962',
  'https://www.isinwheel.com/cdn/shop/files/IMG_20230912_131136-01.jpg?v=1703818026',
  'https://www.isinwheel.com/cdn/shop/files/lADPM4AfJPNTnRTNCADNBIE_1153_2048.jpg?v=1705284628',
  'https://www.isinwheel.com/cdn/shop/files/lADPM4c6Ijzhrf3NCADNBIE_1153_2048_75df4191-0013-4fbc-9618-06f3a59301f4.jpg?v=1705284621',
  'https://www.isinwheel.com/cdn/shop/files/lADPM4OsowTx-Y7NAhzNBWg_1384_540.jpg?v=1703754768',
  'https://www.isinwheel.com/cdn/shop/files/lADPM5HinZgODEDNCADNBgA_1536_2048.jpg?v=1703754624',
  'https://www.isinwheel.com/cdn/shop/files/lADPM5VwHNACNAzNCADNBIE_1153_2048.jpg?v=1705284651',
  'https://www.isinwheel.com/cdn/shop/files/lALPM4rHnrU5q3PNBIHNCAA_2048_1153.png?v=1703754787',
  'https://www.isinwheel.com/cdn/shop/files/lALPM5j9mUhiBLXNBIHNCAA_2048_1153.png?v=1703754812',
  'https://www.isinwheel.com/cdn/shop/files/lQDPJw08oDBfuK3NCADNBgCwpD7iTe2Fkd8GJ0G3uoH_AQ_1536_2048.jpg?v=1715149773',
  'https://www.isinwheel.com/cdn/shop/files/lQDPJwlULmwE6bPNCADNBIGweiCFQY0ETxUFmJo9HUksAA_1153_2048.jpg?v=1705571112',
  'https://www.isinwheel.com/cdn/shop/files/lQDPJxJ0rGbJMG_NBkDNA4Swd4qRV-om1i8Fjyfh9kwwAA_900_1600.jpg?v=1704937089',
  'https://www.isinwheel.com/cdn/shop/files/lQDPJxWvZ0z5gL_NCZnNDmaw9h4SVd8SpBkFiPVp8SPxAA_3686_2457.jpg?v=1704522431',
  'https://www.isinwheel.com/cdn/shop/files/lQDPKeKEJmTWyrPNCADNBIGwdBlmJ8yOQZkFmJo9HUksAw_1153_2048.jpg?v=1705571039',
  'https://www.isinwheel.com/cdn/shop/files/lQDPKHnPJzPlOa3NCADNBIGwuvzwayqHYhwGJ0G3uoH_AA_1153_2048.jpg?v=1715149756',
  'https://www.isinwheel.com/cdn/shop/files/lQDPKHvfkc9HYE3NA7nNAtCwhdq6IYHXG6gGJz8WApg5AA_720_953.jpg?v=1715149741',
  'https://www.isinwheel.com/cdn/shop/files/lQLPJwnSmdpT1A3NCADNBgCwEcWChMJ5NcUGJz8WApg5AQ_1536_2048.jpg?v=1715149712',
]
</script>

<style scoped lang="scss">
.photos-page {
  background: #fff;
}

.photos-shell {
  padding-top: 18px;
  padding-bottom: 34px;
}

.photos-masonry {
  column-count: 3;
  column-gap: 14px;
}

.photo-card {
  break-inside: avoid;
  margin: 0 0 14px;
  overflow: hidden;
  border-radius: 6px;
  background: #f3f3f3;

  img {
    display: block;
    width: 100%;
    height: auto;
    transition:
      transform 0.32s ease,
      filter 0.32s ease;
  }

  &:hover img {
    transform: scale(1.02);
    filter: saturate(1.04);
  }
}

@media (max-width: 900px) {
  .photos-masonry {
    column-count: 2;
  }
}

@media (max-width: 560px) {
  .photos-shell {
    padding-top: 14px;
  }

  .photos-masonry {
    column-count: 2;
    column-gap: 10px;
  }

  .photo-card {
    margin-bottom: 10px;
    border-radius: 4px;
  }
}
</style>
