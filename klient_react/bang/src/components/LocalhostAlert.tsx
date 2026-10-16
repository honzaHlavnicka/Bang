import { useEffect, useState } from "react";

const isRealLocalhost = typeof window !== "undefined" && window.location.hostname.includes('localhost');

const isEnvHostLocalhost = String(import.meta.env.VITE_SERVER_HOST).trim().toLowerCase().includes('localhost');
const isDebug = String(import.meta.env.VITE_DEBUG).trim().toLowerCase() === 'true';

const showAlertWarning = !isRealLocalhost && (isDebug || isEnvHostLocalhost);

export default function LocalhostAlert() {
    if (!showAlertWarning) {
        return null;
    }

    return <TheAlert />;
}

function TheAlert() {
    const [isShown, setIsShown] = useState(true);

    useEffect(() => {
        const handleClose = () => setIsShown(false);

        document.body.addEventListener("click", handleClose);
        document.body.addEventListener("keydown", handleClose);

        return () => {
            document.body.removeEventListener("click", handleClose);
            document.body.removeEventListener("keydown", handleClose);
        };
    }, []);

    if (!isShown) {
        return null;
    }

    return (
        <div
            style={{
                position: 'fixed', top: 0, left: 0, width: '100%', 
                backgroundColor: 'yellow', color: 'black', textAlign: 'center', 
                zIndex: 1000, padding: '10px', cursor: 'pointer'
            }}
            onClick={() => setIsShown(false)}
            aria-hidden="true"
        >
            <p>
                Produkční server pravděpodobně není nakonfigurován správně | The production server is likely misconfigured. <br/>
                Kontaktuj mě prosím na <a href="mailto:dev@honzaa.cz" onClick={(e) => e.stopPropagation()}>dev@honzaa.cz</a>
            </p>
        </div>
    );
}
