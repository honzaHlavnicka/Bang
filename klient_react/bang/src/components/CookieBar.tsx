import { useTranslation } from 'react-i18next';
import { usePostHog } from '@posthog/react';
import config from '../config';
import globalCSS from '../styles/global.module.css';
import css from '../styles/loginPage.module.css';

interface CookieBarProps {
    open: boolean;
    onClose: () => void;
    onOpen: () => void;
}

export default function CookieBar({ open, onClose, onOpen }: CookieBarProps) {
    const { t } = useTranslation();
    const posthog = usePostHog();

    return (
        <>
            {open && (
                <footer className={css.footer}>
                    <div className={css.footerContent}>
                        <p dangerouslySetInnerHTML={{ __html: t("footer.copyright") }} />
                        <nav>
                            <a href="/apidocs" target="_blank" rel="noopener noreferrer">{t("Dokumentace SDK")}</a> | <a href="https://honzaa.cz" target='_blank' >{t("honzaa.cz")}</a> | <a href="https://github.com/honzaHlavnicka/Bang/blob/master/docs/tutorial/VlastniHra.md" target='_blank' >{t("vytvoření pluginu")}</a> | <a href="https://github.com/honzaHlavnicka/Bang" target="_blank">{t("GitHub")}</a> | <a href="https://honzaa.itch.io/card-games" target="_blank">{t("itch.io")}</a>
                        </nav>
                        <small>
                            <p>
                                <strong>{t("Upozornění:")}</strong> <span dangerouslySetInnerHTML={{ __html: t("footer.legal_text") }} />
                            </p>
                        </small>
                        <div style={{display: "flex", gap: "10px", marginTop: "16px", justifyContent: "center"}}>
                            <button onClick={() => { 
                                localStorage.setItem("souhlas", "true"); 
                                posthog?.set_config({ 
                                    persistence: 'localStorage+cookie', 
                                    disable_persistence: false,
                                    disable_session_recording: false,
                                    autocapture: true,
                                    capture_performance: true
                                });
                                posthog?.startSessionRecording();
                                posthog?.opt_in_capturing();
                                posthog?.capture('consent_accepted'); 
                                onClose();
                            }} className={globalCSS.button}>
                                {t("Souhlasím")}
                            </button>
                            <button onClick={() => { 
                                onClose(); 
                                localStorage.setItem("souhlas", "false"); 
                                posthog?.set_config({ 
                                    persistence: 'memory', 
                                    disable_persistence: true,
                                    disable_session_recording: true,
                                    autocapture: false,
                                    capture_performance: false
                                });
                                posthog?.stopSessionRecording();
                                posthog?.opt_out_capturing();
                                posthog?.capture('consent_declined'); 
                            }} className={globalCSS.button} style={{backgroundColor: "rgba(100, 100, 100, 0.5)"}}>
                                {t("Nesouhlasím")}
                            </button>
                        </div>
                    </div>
                </footer>
            )}

            {config.showCookies && !open && (
                <button 
                    onClick={onOpen}
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
