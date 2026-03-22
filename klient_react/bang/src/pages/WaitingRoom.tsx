import DarkModeSwitch from "../components/DarkModeSwitch";
import FlippingCard from "../components/FlippingCard";
import css from "../styles/waitingPage.module.css";

export default function WaitingRoom({ children }: { children: React.ReactNode }) {
    return (
        <div className={css.scrollArea}>
           <DarkModeSwitch style={{position:"fixed",top:10,left:10,zIndex:1005,fontSize:"2em"}}/>

           <div className={css.container}>
                <FlippingCard pauseOnHover={false} frontImage={"/img/karty/bang.png"} backImage={"/img/karty/zezadu.png"}  width={110}/>
                <div className={css.content} >
                    {children}
                </div>
            </div>
        </div>
    );
}