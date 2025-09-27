import css from '../styles/card.module.css';
import { useZoom } from "../modules/ZoomContext";
import { useGame } from '../modules/GameKontext';


type CardProps = {
    image: string;
    name?: string;
    isRotated?: boolean;
    style?: React.CSSProperties;
    biggerOnHover?: boolean;
    animationOnStart?: boolean;
    isInLine?: boolean;
    onClick?: React.MouseEventHandler<HTMLDivElement>;
};

export default function Card({
    image,
    name = (image.match(/([^/\\]+)\.[^/\\]+$/)?.[1]) ?? image, //vrátí pouze jméno souboru. Pokud se soubor jmenuje nejak necekane, tak se vrátí celá cesta.
    isRotated = false,
    style,
    animationOnStart = false,
    biggerOnHover = false,
    isInLine = false,
    onClick,
}: CardProps) {
    const nic = "";
    const { isZoomMode, zoomedCard, setZoomedCard } = useZoom();
    const {socket} = useGame()


    function handleClick(e: React.MouseEvent<HTMLDivElement>) {
        if (isZoomMode) {
            if(zoomedCard == image){
                setZoomedCard(null);
                //TODO: odzoomovat
            }else{
                setZoomedCard(image);
                //TODO: zazoomovat
            }
        }else{
            socket.send("dddd")
        }
        if (onClick) {
            onClick(e);
        }
    }

    return (
        <div onClick={handleClick} className={` ${animationOnStart ? css.animacePrijeti : ""} ${isInLine ? css.jeDole : ""}`}>
            <img className={`${css.karta} ${(biggerOnHover ? css.zvetsitelna : nic)} ${(isRotated ? css.pootocena : nic)} `} style={style} src={image} alt={name ?? undefined} title={name ?? undefined} />
        </div>
    );
}