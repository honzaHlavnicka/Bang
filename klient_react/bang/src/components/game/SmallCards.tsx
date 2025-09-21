type SmallCardCount = 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8;

export default function SmallCards({count,style}: {count: SmallCardCount, style?: React.CSSProperties}) {
    return (
            <img src={"/img/maleKarty/" + count + ".png"} style={{height:"15em",...style}} alt={count + " karet"} />
    );
}