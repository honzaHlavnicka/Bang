//type SmallCardCount = 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8;

export default function SmallCards({ count, style }: { count: number, style?: React.CSSProperties }) {
    if (count < 0) return null;

    if (count > 8) {
        return (
            <div style={{ height: "15em", display: "inline-block", ...style }}>
                <svg 
                    xmlns="http://www.w3.org/2000/svg" 
                    viewBox="-136.543 -381.516 1289.806 1279.096"
                    style={{ height: "100%", width: "auto" }} 
                >
                    <image 
                        href="/img/karty/zezadu.png" 
                        width="700" 
                        height="700" 
                        x="-200" 
                        y="-90" 
                        style={{borderRadius: "10px"}}
                        clipPath="url(#clip)"
                    />
                    <path 
                        d="M 673.375 115.551 H 711.712 V 190.212 H 783.653 V 228.549 H 711.712 V 303.21 H 673.375 V 228.549 H 601.433 V 190.212 H 673.375 Z"  
                        fill="black" 
                        strokeWidth="5" 
                        transform="matrix(0.707107, -0.707107, 0.707107, 0.707107, -170.063569, 2.790687)"
                        style={{ transformOrigin: "692.543px 209.38px" }}
                    />
                    <text 
                        x="630.88" 
                        y="372.664" 
                        fill="black" 
                        style={{ fontFamily: "Arial", fontSize: "430px" }}
                    >
                        {count}
                    </text>
                </svg>
            </div>
        );
    }

    return (
        <img 
            src={`/img/maleKarty/${count}.png`} 
            style={{ height: "15em", ...style }} 
            alt={`${count} karet`} 
        />
    );
}