import { useState, useEffect } from "react";

// FlippingCard.jsx
// React component that shows a continuously rotating 3D card
// with an image on the front and an image on the back.
// Width is configurable, height is automatically adjusted to keep aspect ratio.

export default function FlippingCard({
  frontImage,
  backImage,
  width = 300,
  duration = 3, // seconds for a full 360° rotation
  autoPlay = true,
  pauseOnHover = true,
  className = "",
  altFront = "Front image",
  altBack = "Back image",
}: {
  frontImage: string;
  backImage: string;
  width?: number;
  duration?: number;
  autoPlay?: boolean;
  pauseOnHover?: boolean;
  className?: string;
  altFront?: string;
  altBack?: string;
}) {
  // State to hold the aspect ratio of the front image
  const [aspectRatio, setAspectRatio] = useState(0.666); // fallback 3:2

  useEffect(() => {
    if (frontImage) {
      const img = new Image();
      img.onload = () => {
        setAspectRatio(img.height / img.width);
      };
      img.src = frontImage;
    }
  }, [frontImage]);

  const height = width * aspectRatio;

  return (
    <div
      className={`flipping-card-wrapper inline-block ${className}`}
      style={{ width: `${width}px`, height: `${height}px` }}
      aria-hidden={!autoPlay}
    >
      <div className={`flipping-card-scene`} style={{ width: "100%", height: "100%" }}>
        <div
          className={`flipping-card ${autoPlay ? 'auto' : ''} ${pauseOnHover ? 'pause-on-hover' : ''}`}
          style={{ width: "100%", height: "100%" }}
        >
          {/* front side */}
          <div className="card-side card-front" role="img" aria-label={altFront}>
            <img src={frontImage} alt={altFront} draggable={false} />
          </div>

          {/* back side */}
          <div className="card-side card-back" role="img" aria-label={altBack}>
            <img src={backImage} alt={altBack} draggable={false} />
          </div>
        </div>
      </div>

      <style>{`
        .flipping-card-scene {
          perspective: 1200px;
        }

        .flipping-card {
          position: relative;
          transform-style: preserve-3d;
        }

        .flipping-card.auto {
          animation-name: flip360;
          animation-duration: ${duration}s;
          animation-iteration-count: infinite;
          animation-timing-function: linear;
        }

        .flipping-card.pause-on-hover:hover {
          animation-play-state: paused;
        }

        @keyframes flip360 {
          0%   { transform: rotateY(0deg); }
          25%  { transform: rotateY(90deg); }
          50%  { transform: rotateY(180deg); }
          75%  { transform: rotateY(270deg); }
          100% { transform: rotateY(360deg); }
        }


        .card-side {
          position: absolute;
          top: 0;
          left: 0;
          width: 100%;
          height: 100%;
          display: flex;
          align-items: center;
          justify-content: center;
          backface-visibility: hidden;
          -webkit-backface-visibility: hidden;
          overflow: hidden;
          border-radius: 12px;
          box-shadow: 0 10px 25px rgba(0,0,0,0.12);
        }

        .card-front { transform: rotateY(0deg); }
        .card-back  { transform: rotateY(180deg); }

        .card-side img {
          display: block;
          width: 100%;
          height: 100%;
          object-fit: cover;
          pointer-events: none;
          user-select: none;
        }
      `}</style>
    </div>
  );
}

// USAGE EXAMPLE:
// <FlippingCard
//   frontImage="/images/front.jpg"
//   backImage="/images/back.jpg"
//   width={360}
//   duration={5}
//   autoPlay={true}
//   pauseOnHover={true}
// />
