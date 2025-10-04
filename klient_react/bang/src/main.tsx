import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.tsx'
import { GameProvider } from './modules/GameProvider.tsx'
import { Toaster } from 'react-hot-toast'

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <GameProvider>
      <Toaster position={'top-right'} containerStyle={{fontSize:"1.4em"}}/>
      <App />
    </GameProvider>
  </StrictMode>,
)
