import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import css from "../styles/global.module.css";

const QRConnectButton = ({ code }:{code:string|null}) => {
  const { i18n, t } = useTranslation();
  const [isOpen, setIsOpen] = useState(false);

  const lang = i18n.language || 'cs';

  const gameUrl = `https://bang.honzaa.cz/${lang}?code=${code}&utm_source=bang&utm_medium=qr_code&utm_campaign=connect`;
  const qrApiUrl = `https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=${encodeURIComponent(gameUrl)}`;
  
  if(!code) return null;
  return (
    
    <div style={{ fontFamily: 'sans-serif' }}>
      {/* Tlačítko pro otevření popupu */}
      <button 
        onClick={() => setIsOpen(true)}
        className={css.button}
      >
        {t('Zobrazit QR kód')}
      </button>

      {/* Popup / Modal */}
      {isOpen && (
        <div style={{
          position: 'fixed',
          top: 0,
          left: 0,
          width: '100%',
          height: '100%',
          backgroundColor: 'rgba(0, 0, 0, 0.5)',
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center',
          zIndex: 1000
        }}>
          <div style={{
            backgroundColor: '#fff',
            padding: '30px',
            borderRadius: '10px',
            textAlign: 'center',
            boxShadow: '0 4px 6px rgba(0,0,0,0.1)',
            position: 'relative',
            maxWidth: '90%',
            width: '320px'
          }}>
            <h3 style={{ marginTop: 0, marginBottom: '15px' }}>
              {t('Připoj se naskenováním!')}
            </h3>
            
            {/* Obyčejný obrázek načítaný z API endpointu */}
            <div style={{ margin: '20px 0' }}>
              <img 
                src={qrApiUrl} 
                alt="QR Code" 
                style={{ width: '200px', height: '200px', display: 'block', margin: '0 auto' }} 
              />
            </div>

            {/* Tlačítko pro zavření */}
            <button 
              onClick={() => setIsOpen(false)}
              className={css.button}
            >
              {t('Zavřít')}
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default QRConnectButton;
