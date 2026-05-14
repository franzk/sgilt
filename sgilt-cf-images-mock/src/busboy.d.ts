declare module 'busboy' {
  import { IncomingHttpHeaders } from 'http'
  import { Readable } from 'stream'

  interface BusboyConfig {
    headers: IncomingHttpHeaders
    limits?: { fileSize?: number; files?: number; fields?: number }
  }

  interface FileInfo {
    filename: string
    encoding: string
    mimeType: string
  }

  interface Busboy {
    on(event: 'file', listener: (fieldname: string, stream: Readable, info: FileInfo) => void): this
    on(event: 'finish', listener: () => void): this
    on(event: 'error', listener: (err: Error) => void): this
    on(event: string, listener: (...args: unknown[]) => void): this
    write(chunk: Buffer | string, cb?: (err?: Error | null) => void): boolean
    end(): void
  }

  function busboy(config: BusboyConfig): Busboy
  export = busboy
}
