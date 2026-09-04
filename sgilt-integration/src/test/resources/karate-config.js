function fn() {
  var Sys = Java.type('java.lang.System');
  return {
    baseUrl: Sys.getProperty('karate.baseUrl'),
    notificationsBaseUrl: Sys.getProperty('karate.notificationsBaseUrl'),
    tokenUser: Sys.getProperty('karate.token.user'),
    tokenPro: Sys.getProperty('karate.token.pro'),
    tokenAdmin: Sys.getProperty('karate.token.admin'),
    tokenOrphan: Sys.getProperty('karate.token.orphan'),
    tokenPrestataire: Sys.getProperty('karate.token.prestataire'),
    tokenUser2: Sys.getProperty('karate.token.user2'),
    prestataireId: Sys.getProperty('karate.fixture.prestataireId'),
    prestataireId2: Sys.getProperty('karate.fixture.prestataireId2'),
    mailSendQueueUrl: Sys.getProperty('karate.mailSendQueueUrl'),
    mailQueueAuth: Sys.getProperty('karate.mailQueueAuth')
  };
}
