import { useTranslation } from 'react-i18next';
import toast from 'react-hot-toast';
import globalCSS from '../styles/global.module.css';
import css from '../styles/loginPage.module.css';

interface DonateModalProps {
    isOpen: boolean;
    onClose: () => void;
}

export default function DonateModal({ isOpen, onClose }: DonateModalProps) {
    const { t, i18n } = useTranslation();

    if (!isOpen) return null;

    const copyToClipboard = (text: string, successMessage: string) => {
        navigator.clipboard.writeText(text);
        toast.success(successMessage);
    };

    const iban = import.meta.env.VITE_DONATE_IBAN || "";
    const accNumber = import.meta.env.VITE_DONATE_ACC_NUMBER || "";

    return (
        <div className={css.modalOverlay} onClick={onClose}>
            <div className={css.modalContainer} onClick={(e) => e.stopPropagation()}>
                <div className={css.modalHeader}>
                    <h2>{t("Jak nás podpořit")}</h2>
                    <button className={css.modalCloseButton} onClick={onClose}>&times;</button>
                </div>
                <div className={css.modalBody}>
                    <img src="/img/support-qr.png" alt="QR platba" className={css.qrCodeImg} />
                    
                    {i18n.language.startsWith("en") && (
                        <div className={css.modalWarning}>
                            {t("warning_qr_abroad")}
                        </div>
                    )}

                    <div className={css.accountDetails}>
                        <div className={css.accountRow}>
                            <span className={css.accountLabel}>{t("Číslo účtu")}</span>
                            <div className={css.accountValueContainer}>
                                <span className={css.accountValue}>{accNumber}</span>
                                <button 
                                    className={globalCSS.button}
                                    style={{ padding: "6px 12px", fontSize: "12px", borderRadius: "6px", margin: 0 }}
                                    onClick={() => copyToClipboard(accNumber, t("Číslo účtu zkopírováno do schránky"))}
                                >
                                    {t("Zkopírovat")}
                                </button>
                            </div>
                        </div>
                        <div className={css.accountRow}>
                            <span className={css.accountLabel}>{t("IBAN")}</span>
                            <div className={css.accountValueContainer}>
                                <span className={css.accountValue}>{iban}</span>
                                <button 
                                    className={globalCSS.button}
                                    style={{ padding: "6px 12px", fontSize: "12px", borderRadius: "6px", margin: 0 }}
                                    onClick={() => copyToClipboard(iban, t("IBAN zkopírován do schránky"))}
                                >
                                    {t("Zkopírovat")}
                                </button>
                            </div>
                        </div>
                    </div>
                    
                    <div className={css.modalInfo}>
                        {t("voluntary_note")}
                    </div>
                </div>
            </div>
        </div>
    );
}
