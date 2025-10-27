import css from '../styles/card.module.css';
import { useZoom } from "../modules/ZoomContext";

export type CardSizeType = "NORMAL"|"SMALL"|"BIG"

type CardProps = {
    image: string;
    name?: string;
    isRotated?: boolean;
    style?: React.CSSProperties;
    biggerOnHover?: boolean;
    animationOnStart?: boolean;
    isInLine?: boolean;
    id?:number;
    onClick?: React.MouseEventHandler<HTMLDivElement>;
    size?:CardSizeType
};

export default function Card({
    image,
    name = (image.match(/([^/\\]+)\.[^/\\]+$/)?.[1]) ?? image, //vrátí pouze jméno souboru. Pokud se soubor jmenuje nejak necekane, tak se vrátí celá cesta.
    isRotated = false,
    style,
    animationOnStart = false,
    biggerOnHover = false,
    isInLine = false,
    id,
    onClick,
    size = "NORMAL"
}: CardProps) {
    const nic = "";
    const { isZoomMode, zoomedCard, setZoomedCard } = useZoom();

    const key = typeof id === 'number' ? id : -1;

    function handleClick(e: React.MouseEvent<HTMLDivElement>) {
        if (isZoomMode) {
            if(zoomedCard == image){
                setZoomedCard(null);
            }else{
                setZoomedCard(image);
            }
        }else if (onClick) {
            onClick(e);
        }
    }

    return (
        <div data-id={key} onClick={handleClick} className={` ${animationOnStart ? css.animacePrijeti : ""} ${isInLine ? css.jeDole : ""}`}>
            <img className={`${css.karta} ${(biggerOnHover ? css.zvetsitelna : nic)} ${(isRotated ? css.pootocena : nic)} `} style={style} src={image} alt={name ?? undefined} title={name ?? undefined} />
        </div>
    );
}