const PROXY_TARGET = 'http://localhost:8084';

module.exports = [
  {
    context: ['/api', '/uploads'],
    target: PROXY_TARGET,
    secure: false,
    changeOrigin: true,
    logLevel: 'info',
    onProxyRes: function (proxyRes) {
      if (proxyRes && proxyRes.headers) {
        // Remove any authentication challenge headers to prevent browser login prompt
        for (const h of Object.keys(proxyRes.headers)) {
          const name = h.toLowerCase();
          if (name === 'www-authenticate' || name === 'proxy-authenticate') {
            delete proxyRes.headers[h];
          }
        }
      }
    }
  }
];
