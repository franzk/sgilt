import { SMTPServer, SMTPServerDataStream } from 'smtp-server'
import { simpleParser, ParsedMail } from 'mailparser'
import amqp from 'amqp-connection-manager'
import type { ChannelWrapper } from 'amqp-connection-manager'
import type { Channel } from 'amqplib'
import dotenv from 'dotenv'

const MAIL_SEND_QUEUE = 'mail.send'
const MAIL_SEND_DLQ = 'mail.send.dlq'

interface MailRequest {
  to: string
  mailType: 'RAW_EMAIL'
  context: {
    subject: string
    html: string
  }
}

dotenv.config()

if (!process.env.RABBITMQ_URL) {
  throw new Error('RABBITMQ_URL is not set')
}

/**
 * Connexion RabbitMQ persistante — reconnexion et redéclaration de la queue automatiques
 * en cas de coupure (gérées par amqp-connection-manager).
 */
const connection = amqp.connect([process.env.RABBITMQ_URL])
const channelWrapper: ChannelWrapper = connection.createChannel({
  setup: (channel: Channel) =>
    channel.assertQueue(MAIL_SEND_QUEUE, {
      durable: true,
      arguments: {
        'x-dead-letter-exchange': '',
        'x-dead-letter-routing-key': MAIL_SEND_DLQ
      }
    })
})

/**
 * SMTP server to receive emails and forward them to the API
 */
const server = new SMTPServer({
  authOptional: true,
  onData(stream, session, callback) {
    handleRequest(stream)
      .then(() => callback())
      .catch((err) => callback(err))
  }
})

server.listen(1025, () => {
  console.log('📨 SMTP Gateway listening on port 1025')
})

/**
 * Handle incoming email and forward it to the API
 * @param {SMTPServerDataStream} stream - The email stream
 */
const handleRequest = async (stream: SMTPServerDataStream) => {
  try {
    const parsed: ParsedMail = await simpleParser(stream)

    console.log('📧 Received email:', parsed.from?.text, parsed.subject)

    // process mail data
    const payload: MailRequest = {
      to: Array.isArray(parsed.to)
        ? parsed.to.flatMap((addr) => addr.text).join(',')
        : parsed.to?.text || '',
      mailType: 'RAW_EMAIL',
      context: {
        subject: parsed.subject || '',
        html: parsed.html || parsed.textAsHtml || ''
      }
    }

    // Publish to sgilt-mailer's queue (contrat MailRequest de sgilt-mailer)
    await channelWrapper.sendToQueue(MAIL_SEND_QUEUE, Buffer.from(JSON.stringify(payload)), {
      persistent: true,
      contentType: 'application/json'
    })
    console.log(`✅ Mail forwarded to queue: ${payload.context.subject}`)
  } catch (err) {
    console.error('❌ Error handling email:', err)
  }
}
