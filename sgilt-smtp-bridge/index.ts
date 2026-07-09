import { SMTPServer, SMTPServerDataStream } from 'smtp-server'
import { simpleParser, ParsedMail } from 'mailparser'
import axios from 'axios'
import dotenv from 'dotenv'


interface MailRequest {
  to: string
  mailType: 'RAW_EMAIL'
  context: {
    subject: string
    html: string
  }
}

dotenv.config()

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
  if (!process.env.MAILER_URL) {
    throw new Error('MAILER_URL is not set')
  }
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

    // Send email to API (contrat MailRequest de sgilt-mailer)
    await axios.post(process.env.MAILER_URL!, payload)
    console.log(`✅ Mail forwarded to API: ${payload.context.subject}`)
  } catch (err) {
    console.error('❌ Error handling email:', err)
  }
}
