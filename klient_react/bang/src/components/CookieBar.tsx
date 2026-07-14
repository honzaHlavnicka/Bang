import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { usePostHog } from '@posthog/react';
import config from '../config';
import styles from '../styles/cookieBar.module.css';

interface CookieBarProps {
    open: boolean;
    onClose: () => void;
    onOpen: () => void;
}

export default function CookieBar({ open, onClose, onOpen }: CookieBarProps) {
    const { t } = useTranslation();
    const posthog = usePostHog();
    const [showDetails, setShowDetails] = useState(false);

    return (
        <>
            {/* Gorgeous floating cookie consent banner */}
            {open && (
                <div className={styles.cookieBanner}>
                    <h3 className={styles.title}>
                        <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round" style={{ color: 'orange' }}>
                            <path d="M12 2a10 10 0 1 0 10 10 4 4 0 0 1-5-5 4 4 0 0 1-5-5" />
                            <path d="M8.5 8.5v.01" />
                            <path d="M16 15.5v.01" />
                            <path d="M12 12v.01" />
                            <path d="M11 17v.01" />
                            <path d="M7 14v.01" />
                        </svg>
                        {t("cookie_bar.title")}
                    </h3>
                    
                    <p className={styles.text}>
                        {t("cookie_bar.text")}
                    </p>

                    <div>
                        <button 
                            type="button" 
                            className={styles.detailsToggle} 
                            onClick={() => {
                                const nextShow = !showDetails;
                                setShowDetails(nextShow);
                                posthog?.capture('cookie_bar_details_toggled', { show_details: nextShow });
                            }}
                        >
                            {showDetails ? t("cookie_bar.hide_details") : t("cookie_bar.show_details")}
                        </button>
                        
                        {showDetails && (
                            <div className={styles.detailsContent}>
                                <strong>{t("Upozornění:")}</strong>{' '}
                                <span dangerouslySetInnerHTML={{ __html: t("footer.legal_text") }} />
                            </div>
                        )}
                    </div>
 
                    <div className={styles.buttonContainer}>
                        <button 
                            onClick={() => { 
                                onClose(); 
                                localStorage.setItem("souhlas", "false"); 
                                posthog?.capture('consent_declined'); 
                                posthog?.set_config({ 
                                    persistence: 'memory', 
                                    disable_persistence: true,
                                    disable_session_recording: true,
                                    autocapture: false,
                                    capture_performance: false
                                });
                                posthog?.stopSessionRecording();
                            }} 
                            className={styles.btnDecline}
                        >
                            {t("Nesouhlasím")}
                        </button>
                        
                        <button 
                            onClick={() => { 
                                localStorage.setItem("souhlas", "true"); 
                                posthog?.set_config({ 
                                    persistence: 'localStorage+cookie', 
                                    disable_persistence: false,
                                    disable_session_recording: false,
                                    autocapture: true,
                                    capture_performance: true
                                });
                                posthog?.startSessionRecording();
                                posthog?.capture('consent_accepted'); 
                                onClose();
                            }} 
                            className={styles.btnAccept}
                        >
                            {t("Souhlasím")}
                        </button>
                    </div>
                </div>
            )}

            {/* Small floating privacy settings icon button */}
            {config.showCookies && !open && (
                <button 
                    onClick={() => {
                        posthog?.capture('cookie_bar_reopened');
                        onOpen();
                    }}
                    title={t("Nastavení soukromí")}
                    style={{
                        position: "fixed",
                        bottom: "15px",
                        left: "15px",
                        zIndex: 1000,
                        width: "40px",
                        height: "40px",
                        borderRadius: "50%",
                        background: "rgba(0, 0, 0, 0.5)",
                        border: "1px solid rgba(255, 255, 255, 0.2)",
                        cursor: "pointer",
                        display: "flex",
                        alignItems: "center",
                        justifyContent: "center",
                        padding: "0",
                        transition: "transform 0.2s, background 0.2s",
                        boxShadow: "0 2px 10px rgba(0,0,0,0.3)"
                    }}
                    onMouseEnter={(e) => {
                        e.currentTarget.style.background = "rgba(0, 0, 0, 0.7)";
                        e.currentTarget.style.transform = "scale(1.1)";
                    }}
                    onMouseLeave={(e) => {
                        e.currentTarget.style.background = "rgba(0, 0, 0, 0.5)";
                        e.currentTarget.style.transform = "scale(1)";
                    }}
                >
                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" style={{color: "#fff"}}>
                        <path d="M12 2a10 10 0 1 0 10 10 4 4 0 0 1-5-5 4 4 0 0 1-5-5"/>
                        <path d="M8.5 8.5v.01"/>
                        <path d="M16 15.5v.01"/>
                        <path d="M12 12v.01"/>
                        <path d="M11 17v.01"/>
                        <path d="M7 14v.01"/>
                    </svg>
                </button>
            )}
        </>
    );
}
