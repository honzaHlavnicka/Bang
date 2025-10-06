import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.tsx'
import { GameProvider } from './modules/GameProvider.tsx'
import { Toaster } from 'react-hot-toast'
import { DialogProvider } from './modules/DialogProvider.tsx'
import Dialog from './components/Dialog.tsx'

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <DialogProvider>
      <GameProvider>
        <Dialog />
        <Toaster position={'top-right'} containerStyle={{fontSize:"1.4em"}}/>
        <App />
      </GameProvider>
    </DialogProvider>
  </StrictMode>,
)
