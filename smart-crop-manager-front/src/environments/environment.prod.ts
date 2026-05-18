export const environment = {
  production: true,
  apiUrl: typeof window !== 'undefined' ? `http://${window.location.hostname}:8080` : 'http://backend:8099'
};