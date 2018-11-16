package com.reign.gcld.battle.scene;

public class BuilderFactory
{
    private static final BuilderFactory instance;
    private Builder oneVsNpcBuilder;
    private Builder manyVsNpcBuilder;
    private Builder cityBuilder;
    private Builder cityNpcBuilder;
    private Builder occupyBuilder;
    private Builder groupMineBuilder;
    private Builder personalMineBuilder;
    private Builder nationRankBuilder;
    private Builder oneVsRewardNpcBuilder;
    private Builder oneVsExtraNpcBuilder;
    private Builder cityOneToOneBuilder;
    private Builder barbarainBuilder;
    private Builder barbarinOneToOneBuilder;
    private Builder duelBuilder;
    private Builder cityEventBuilder;
    private Builder scenarioBuilder;
    private Builder scenarioOneToOneBuilder;
    private Builder scenarioEventBuilder;
    
    static {
        instance = new BuilderFactory();
    }
    
    private BuilderFactory() {
        this.oneVsNpcBuilder = new OneVsNpcBuilder(1);
        this.manyVsNpcBuilder = new ManyVsNpcBuilder(2);
        this.cityBuilder = new CityBuilder(3);
        this.cityNpcBuilder = new CityNpcBuilder(10);
        this.occupyBuilder = new PositionBuilder(4);
        this.groupMineBuilder = new GroupMineBuider(7);
        this.personalMineBuilder = new PersonalMineBuider(6);
        this.nationRankBuilder = new NationRankBuider(8);
        this.oneVsRewardNpcBuilder = new OneVsRewardNpcBuilder(11);
        this.oneVsExtraNpcBuilder = new OneVsExtraBuilder(12);
        this.cityOneToOneBuilder = new CityOneToOneBuilder(13);
        this.barbarainBuilder = new BarbarainBuilder(14);
        this.barbarinOneToOneBuilder = new BarbarainOneToOneBuilder(15);
        this.duelBuilder = new DuelBuilder(16);
        this.cityEventBuilder = new CityEventBuilder(17);
        this.scenarioBuilder = new ScenarioBuilder(18);
        this.scenarioOneToOneBuilder = new ScenarioOneToOneBuilder(19);
        this.scenarioEventBuilder = new ScenarioEventBuilder(20);
    }
    
    public static BuilderFactory getInstance() {
        return BuilderFactory.instance;
    }
    
    public Builder getBuilder(final int battleType) {
        switch (battleType) {
            case 1:
            case 5: {
                return this.oneVsNpcBuilder;
            }
            case 2: {
                return this.manyVsNpcBuilder;
            }
            case 4: {
                return this.occupyBuilder;
            }
            case 3: {
                return this.cityBuilder;
            }
            case 7: {
                return this.groupMineBuilder;
            }
            case 6: {
                return this.personalMineBuilder;
            }
            case 10: {
                return this.cityNpcBuilder;
            }
            case 8: {
                return this.nationRankBuilder;
            }
            case 11: {
                return this.oneVsRewardNpcBuilder;
            }
            case 12: {
                return this.oneVsExtraNpcBuilder;
            }
            case 13: {
                return this.cityOneToOneBuilder;
            }
            case 14: {
                return this.barbarainBuilder;
            }
            case 15: {
                return this.barbarinOneToOneBuilder;
            }
            case 16: {
                return this.duelBuilder;
            }
            case 17: {
                return this.cityEventBuilder;
            }
            case 18: {
                return this.scenarioBuilder;
            }
            case 19: {
                return this.scenarioOneToOneBuilder;
            }
            case 20: {
                return this.scenarioEventBuilder;
            }
            default: {
                return null;
            }
        }
    }
}
