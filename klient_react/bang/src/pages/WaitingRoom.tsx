import FlippingCard from "../components/FlippingCard";
import css from "../styles/waitingPage.module.css";

export default function WaitingRoom({ children }: { children: React.ReactNode }) {
    return (
        <div>
           <div className={css.container}>
                <FlippingCard pauseOnHover={false} frontImage={"/img/karty/bang.png"} backImage={"/img/karty/zezadu.png"}  width={110}/>

                {/*<div className="spinner" style={{
                width: 70,
                height: 70,
                border: '6px solid #ccc',
                borderTop: '6px solid #1976d2',
                borderRadius: '50%',
                animation: 'spin 1s linear infinite'
                }} />*/}
                <div className={css.content} >
                {children}
                </div>
            </div>
        </div>
    );
}