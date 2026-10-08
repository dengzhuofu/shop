export default defineNuxtRouteMiddleware((to) => {
  const path = to.path.replace(/isinwheel/ig, 'cbjj')
  if (path !== to.path) return navigateTo({ path, query: to.query, hash: to.hash }, { redirectCode: 301, replace: true })
})
