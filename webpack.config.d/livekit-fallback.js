// livekit-client pulls in a Node-only fallback path (via its polyfill deps) that
// references the 'os'/'path' core modules. That code path never executes in the
// browser, but webpack 5 no longer auto-polyfills Node core modules, so without
// this it fails to resolve them at bundle time.
config.resolve = config.resolve || {};
config.resolve.fallback = Object.assign({}, config.resolve.fallback, {
    os: false,
    path: false
});
