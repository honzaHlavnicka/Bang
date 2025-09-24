import { createContext, useContext, useState } from "react";

const defaultZoomContext: ZoomContextType = {
    isZoomMode: false,
    zoomedCard: null,
    setZoomedCard: () => {},
    toggleZoomMode: () => {},
};

const ZoomContext = createContext<ZoomContextType>(defaultZoomContext);

type ZoomContextType = {
    isZoomMode: boolean;
    zoomedCard: any;
    setZoomedCard: React.Dispatch<React.SetStateAction<any>>;
    toggleZoomMode: () => void;
};

export function ZoomProvider({ children }: { children: React.ReactNode }) {
    const [isZoomMode, setIsZoomMode] = useState(false);
    const [zoomedCard, setZoomedCard] = useState<any>(null);

    const toggleZoomMode = () => {
        setIsZoomMode((prev) => !prev);
        setZoomedCard(null);
    };

    const value: ZoomContextType = {
        isZoomMode,
        zoomedCard,
        setZoomedCard,
        toggleZoomMode,
    };

    return <ZoomContext.Provider value={value}>{children}</ZoomContext.Provider>;
}

export function useZoom() {
  return useContext(ZoomContext);
}
