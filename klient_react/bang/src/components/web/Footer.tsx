import { t } from "i18next";
import css from "../../styles/loginPage.module.css";
import DonateModal from "../DonateModal";
import { useState } from "react";
export default function Footer() {

    const [donateOpen, setDonateOpen] = useState(false);

    return (
    <>
        <footer className={css.footer + " " + css.context}> 
            <div className={css.footerContent}>
                <p dangerouslySetInnerHTML={{ __html: t("footer.copyright") }} />
                <nav className={css.footerNav}>
                    <a href="/apidocs" target="_blank" rel="noopener noreferrer">{t("Dokumentace SDK")}</a>
                    <span className={css.separator}>•</span>
                    <a href="https://honzaa.cz" target="_blank" rel="noopener noreferrer">{t("honzaa.cz")}</a>
                    <span className={css.separator}>•</span>
                    <a href="https://github.com/honzaHlavnicka/Bang/blob/master/docs/tutorial/VlastniHra.md" target="_blank" rel="noopener noreferrer">{t("vytvoření pluginu")}</a>
                    <span className={css.separator}>•</span>
                    <a href="https://github.com/honzaHlavnicka/Bang" target="_blank" rel="noopener noreferrer">{t("GitHub")}</a>
                    <span className={css.separator}>•</span>
                    <a href="https://discord.gg/WYmtDmMdJS" target="_blank" rel="noopener noreferrer">{t("Discord")}</a>
                    <span className={css.separator}>•</span>
                    <a href="https://honzaa.itch.io/card-games" target="_blank" rel="noopener noreferrer">{t("itch.io")}</a>
                    <span className={css.separator}>•</span>
                    <a href="/privacy" target="_blank" rel="noopener noreferrer">{t("Podmínky a soukromí")}</a>
                    <span className={css.separator}>•</span>
                    {import.meta.env.VITE_DONATE_ACTIVE !== 'false' && <button onClick={() => setDonateOpen(true)} className={css.linkButton}>{t("Podpořit")}</button>}
                </nav>
            </div>
        </footer>
    <DonateModal isOpen={donateOpen} onClose={() => setDonateOpen(false)} />
    </>

    );
}