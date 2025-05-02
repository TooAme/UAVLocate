declare module '@/utils/websocket' {
  export function connectVideoSocket(callback: (frameData: string) => void): {
    onStompError: () => void;
    deactivate: () => void;
  };
}