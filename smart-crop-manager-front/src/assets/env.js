(function (window) {
  window['env'] = window['env'] || {};
  // En desarrollo local este archivo puede quedarse vacío o con valores por defecto.
  // En producción, Docker/Nginx sobreescribirá este archivo inyectando las variables reales del VPS.
})(this);
