package com.wxxtfxrmx.pirates.screen.levelv2.world;

public enum TileType {
    COIN("coin_sheet"),
    BOMB("bomb_sheet"),
    HELM("helm_sheet"),
    @Deprecated
    SAMPLE("sample"),
    REPAIR("repair_sheet");

    private final String atlasPath;

    TileType(String atlasPath) {
        this.atlasPath = atlasPath;
    }

    public String getAtlasPath() {
        return atlasPath;
    }
}
