import css from '../styles/card.module.css';

export default function Card({image, name = image, isBackside = false, style,animationOnStart = false, biggerOnHover=false, isInLine = false}: {image: string, name?: string, isBackside?: boolean, style?: React.CSSProperties, biggerOnHover?: boolean, animationOnStart?:boolean, isInLine?:boolean}) {
    const nic = "";
    return (
        <div className={` ${animationOnStart ? css.animacePrijeti : ""} ${isInLine ? css.jeDole : ""}`}>
            <img className={`${css.karta} ${(biggerOnHover ? css.zvetsitelna : nic)} `} style={style} src={image} alt={name} title={name} />
        </div>
    );
}