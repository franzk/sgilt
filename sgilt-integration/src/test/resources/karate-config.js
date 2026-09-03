function fn() {
  var Sys = Java.type('java.lang.System');
  return {
    baseUrl: Sys.getProperty('karate.baseUrl'),
    tokenUser: Sys.getProperty('karate.token.user'),
    tokenPro: Sys.getProperty('karate.token.pro'),
    tokenAdmin: Sys.getProperty('karate.token.admin'),
    tokenOrphan: Sys.getProperty('karate.token.orphan')
  };
}
