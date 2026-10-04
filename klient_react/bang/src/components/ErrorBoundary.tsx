import { Component, type ErrorInfo, type ReactNode } from 'react';
import WaitingRoom from '../pages/WaitingRoom';

interface Props {
  children: ReactNode;
}

interface State {
  hasError: boolean;
  error: Error | null;
}

export class ErrorBoundary extends Component<Props, State> {
  public override state: State = {
    hasError: false,
    error: null,
  };

  public static getDerivedStateFromError(error: Error): State {
    return { hasError: true, error };
  }

  public override componentDidCatch(error: Error, errorInfo: ErrorInfo) {
    console.error('Nezachycená chyba v React komponentě:', error, errorInfo);

    // Pokud jde o chybu načítání CSS/JS chunků (nový deployment), zkusíme reload
    const isChunkError =
      error?.message?.includes('Unable to preload CSS') ||
      error?.message?.includes('Failed to fetch dynamically imported module') ||
      error?.message?.includes('Loading chunk');

    if (isChunkError) {
      const reloadKey = 'error_boundary_chunk_reload';
      const lastReload = sessionStorage.getItem(reloadKey);
      const now = Date.now();
      if (!lastReload || now - parseInt(lastReload, 10) > 15000) {
        sessionStorage.setItem(reloadKey, String(now));
        window.location.reload();
      }
    }
  }

  private handleReload = () => {
    window.location.reload();
  };

  public override render() {
    if (this.state.hasError) {
      return (
        <WaitingRoom>
          <h1>Něco se pokazilo</h1>
          <p>
            Došlo k neočekávané chybě při načítání rozhraní. Obvykle pomůže obnovit stránku pro načtení nejnovější verze hry.
          </p>
          <button
            onClick={this.handleReload}
            style={{
              padding: '10px 20px',
              fontSize: '1.2em',
              marginTop: '15px',
              cursor: 'pointer',
              borderRadius: '8px',
              backgroundColor: '#d6b058',
              color: '#000',
              border: 'none',
              fontWeight: 'bold',
            }}
          >
            Obnovit stránku (Reload)
          </button>
        </WaitingRoom>
      );
    }

    return this.props.children;
  }
}
