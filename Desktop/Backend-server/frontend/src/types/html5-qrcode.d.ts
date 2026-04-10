declare module 'html5-qrcode' {
  export class Html5QrcodeScanner {
    constructor(
      elementId: string,
      config?: { fps?: number; qrbox?: number | { width: number; height: number } },
      verbose?: boolean
    );

    render(
      successCallback: (decodedText: string, decodedResult?: any) => void,
      errorCallback?: (errorMessage: string) => void
    ): void;

    clear(): Promise<void> | void;
  }
}
