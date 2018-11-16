package com.reign.kf.comm.protocol;

import java.util.concurrent.atomic.*;
import org.codehaus.jackson.type.*;
import org.codehaus.jackson.map.type.*;
import com.reign.kf.comm.util.*;
import java.util.concurrent.*;
import com.reign.kf.comm.entity.gw.*;
import com.reign.kf.comm.entity.auction.*;
import com.reign.kf.comm.param.gw.*;
import java.sql.*;
import com.reign.kf.comm.param.auction.*;
import com.reign.kf.comm.entity.match.*;
import com.reign.kf.comm.entity.*;
import com.reign.kf.comm.entity.kfwd.request.*;
import com.reign.kf.comm.param.match.*;
import com.reign.kf.comm.entity.kfwd.response.*;
import com.reign.kfgz.dto.response.*;
import com.reign.kfgz.dto.request.*;
import com.reign.kfzb.dto.request.*;
import com.reign.kfzb.dto.response.*;
import com.reign.kf.comm.exception.*;
import java.util.*;
import org.codehaus.jackson.*;
import org.codehaus.jackson.map.*;
import java.io.*;

public class Types
{
    public static final ObjectMapper OBJECT_MAPPER;
    private static final AtomicInteger ID;
    private static final HashMap<Integer, JavaType> id2JavaType;
    private static final HashMap<JavaType, Integer> javaType2Id;
    private static final HashMap<Class<?>, Integer> type2Id;
    public static final HashMap<Integer, ObjectReader> id2ObjectReader;
    public static final HashMap<JavaType, ObjectReader> javaType2ObjectReader;
    public static final HashMap<Class<?>, ObjectReader> clazz2ObjectReader;
    public static final JavaType JAVATYPE_REQUESTLIST;
    public static final JavaType JAVATYPE_RESPONSELIST;
    public static final JavaType JAVATYPE_AUCTIONGENERALENTITYLIST;
    public static final JavaType JAVATYPE_SYNCPARAMLIST;
    public static final JavaType JAVATYPE_NEWAUCTIONGENERALENTITYLIST;
    public static final JavaType JAVATYPE_SEASONINFOENTIRYLIST;
    public static final JavaType JAVATYPE_MATCHRANKENTITYLIST;
    public static final JavaType JAVATYPE_MATCHRESULTENTITYLIST;
    public static final JavaType JAVATYPE_MATCHSCHEDULEENTIRYLIST;
    public static final JavaType JAVATYPE_CAMPARMYDATALIST;
    public static final JavaType JAVATYPE_SIMPLEGENERALINFOLIST;
    public static final JavaType JAVATYPE_KFWDTICKETRESULTINFOLIST;
    public static final JavaType JAVATYPE_KFWDTICKETMARKETINFOLIST;
    public static final JavaType JAVATYPE_KFWDRANKINGREWARDINFO;
    public static final JavaType JAVATYPE_KFWDPLAYERINFO;
    public static final JavaType JAVATYPE_KFGZ_RES_RECORD;
    public static final JavaType KFGZRULEINFORES_MAP;
    public static final JavaType KFGZBATTLEREWARDRES_MAP;
    public static final JavaType KFGZENDREWARDRES_MAP;
    public static final JavaType KFGZREWARDRES_LIST;
    public static final JavaType KFGZSCHEDULEINFORES_LIST;
    public static final JavaType KFGZPLAYERRESULTINFO_LIST;
    public static final JavaType KFGZNATIONRESULTREQ_LIST;
    public static final JavaType KFGZPLAYERRANKINGINFOREQ_LIST;
    public static final JavaType KFGZMAILDTO_LIST;
    public static final JavaType KFZBBATTLEINFO_LIST;
    public static final JavaType KFZBBATTLEINFO_MAP;
    public static final JavaType KFZBPHASE2MATCHKEY_LIST;
    public static final JavaType FRAMEBATTLEREPORT_LIST;
    public static final JavaType KFZBPHASE1REWARDINFO_LIST;
    public static final JavaType KFZBPLAYERLIMIT_LIST;
    public static final JavaType KFZBPLAYERGROUP_LIST;
    public static final JavaType KFZBTOPPLAYERINFO_LIST;
    public static final JavaType KFZBROOMINFO_LIST;
    public static final JavaType KFZBFEASTORGANIZER_MAP;
    public static final JavaType KFZBFEASTPARTICIPATOR_LIST;
    public static final JavaType KFZBTREASUREREWARD_LIST;
    public static final JavaType KFWDRANKTREASUREINFO_LIST;
    
    static {
        ID = new AtomicInteger(1);
        id2JavaType = new HashMap<Integer, JavaType>();
        javaType2Id = new HashMap<JavaType, Integer>();
        type2Id = new HashMap<Class<?>, Integer>();
        id2ObjectReader = new HashMap<Integer, ObjectReader>();
        javaType2ObjectReader = new HashMap<JavaType, ObjectReader>();
        clazz2ObjectReader = new HashMap<Class<?>, ObjectReader>();
        JAVATYPE_REQUESTLIST = TypeFactory.collectionType(List.class, Request.class);
        JAVATYPE_RESPONSELIST = TypeFactory.collectionType(List.class, Response.class);
        JAVATYPE_AUCTIONGENERALENTITYLIST = TypeFactory.collectionType(List.class, AuctionGeneralEntity.class);
        JAVATYPE_SYNCPARAMLIST = TypeFactory.collectionType(List.class, SyncParam.class);
        JAVATYPE_NEWAUCTIONGENERALENTITYLIST = TypeFactory.collectionType(List.class, NewAuctionGeneralEntity.class);
        JAVATYPE_SEASONINFOENTIRYLIST = TypeFactory.collectionType(List.class, SeasonInfoEntity.class);
        JAVATYPE_MATCHRANKENTITYLIST = TypeFactory.collectionType(List.class, MatchRankEntity.class);
        JAVATYPE_MATCHRESULTENTITYLIST = TypeFactory.collectionType(List.class, MatchResultEntity.class);
        JAVATYPE_MATCHSCHEDULEENTIRYLIST = TypeFactory.collectionType(List.class, MatchScheduleEntity.class);
        JAVATYPE_CAMPARMYDATALIST = TypeFactory.arrayType(CampArmyParam.class);
        JAVATYPE_SIMPLEGENERALINFOLIST = TypeFactory.collectionType(List.class, KfwdSimpleGInfo.class);
        JAVATYPE_KFWDTICKETRESULTINFOLIST = TypeFactory.collectionType(List.class, KfwdTicketResultInfo.class);
        JAVATYPE_KFWDTICKETMARKETINFOLIST = TypeFactory.collectionType(List.class, KfwdTicketMarketInfo.class);
        JAVATYPE_KFWDRANKINGREWARDINFO = TypeFactory.collectionType(List.class, KfwdRankingRewardInfo.class);
        JAVATYPE_KFWDPLAYERINFO = TypeFactory.collectionType(List.class, KfwdPlayerInfo.class);
        JAVATYPE_KFGZ_RES_RECORD = TypeFactory.collectionType(List.class, Tuple.class);
        KFGZRULEINFORES_MAP = TypeFactory.mapType(Map.class, TypeFactory.fastSimpleType(Integer.class), TypeFactory.fastSimpleType(KfgzRuleInfoRes.class));
        KFGZBATTLEREWARDRES_MAP = TypeFactory.mapType(Map.class, TypeFactory.fastSimpleType(Integer.class), TypeFactory.fastSimpleType(KfgzBattleRewardRes.class));
        KFGZENDREWARDRES_MAP = TypeFactory.mapType(Map.class, TypeFactory.fastSimpleType(Integer.class), TypeFactory.fastSimpleType(KfgzEndRewardRes.class));
        KFGZREWARDRES_LIST = TypeFactory.collectionType(List.class, KfgzRewardRes.class);
        KFGZSCHEDULEINFORES_LIST = TypeFactory.collectionType(List.class, KfgzScheduleInfoRes.class);
        KFGZPLAYERRESULTINFO_LIST = TypeFactory.collectionType(List.class, KfgzPlayerResultInfo.class);
        KFGZNATIONRESULTREQ_LIST = TypeFactory.collectionType(List.class, KfgzNationResultReq.class);
        KFGZPLAYERRANKINGINFOREQ_LIST = TypeFactory.collectionType(List.class, KfgzPlayerRankingInfoReq.class);
        KFGZMAILDTO_LIST = TypeFactory.collectionType(LinkedBlockingQueue.class, MailDto.class);
        KFZBBATTLEINFO_LIST = TypeFactory.collectionType(List.class, KfzbBattleInfo.class);
        KFZBBATTLEINFO_MAP = TypeFactory.mapType(Map.class, TypeFactory.fastSimpleType(Integer.class), TypeFactory.fastSimpleType(KfzbBattleInfo.class));
        KFZBPHASE2MATCHKEY_LIST = TypeFactory.collectionType(List.class, KfzbPhase2MatchKey.class);
        FRAMEBATTLEREPORT_LIST = TypeFactory.collectionType(List.class, FrameBattleReport.class);
        KFZBPHASE1REWARDINFO_LIST = TypeFactory.collectionType(List.class, KfzbPhase1RewardInfo.class);
        KFZBPLAYERLIMIT_LIST = TypeFactory.collectionType(List.class, KfzbPlayerLimit.class);
        KFZBPLAYERGROUP_LIST = TypeFactory.collectionType(List.class, KfzbPlayerGroup.class);
        KFZBTOPPLAYERINFO_LIST = TypeFactory.collectionType(List.class, KfzbTopPlayerInfo.class);
        KFZBROOMINFO_LIST = TypeFactory.collectionType(List.class, KfzbRoomInfo.class);
        KFZBFEASTORGANIZER_MAP = TypeFactory.mapType(Map.class, TypeFactory.fastSimpleType(Integer.class), TypeFactory.fastSimpleType(KfzbFeastOrganizer.class));
        KFZBFEASTPARTICIPATOR_LIST = TypeFactory.collectionType(List.class, KfzbFeastParticipator.class);
        KFZBTREASUREREWARD_LIST = TypeFactory.collectionType(List.class, KfzbTreasureReward.class);
        KFWDRANKTREASUREINFO_LIST = TypeFactory.collectionType(List.class, KfwdRankTreasureInfo.class);
        OBJECT_MAPPER = new ObjectMapper();
        register();
    }
    
    private static void register() {
        register(Request.class);
        register(Response.class);
        register(RequestChunk.class);
        register(CommEntity.class);
        register(ExceptionEntity.class);
        register(AuctionInfoEntity.class);
        register(AuctionGeneralEntity.class);
        register(AuctionSignGeneralEntity.class);
        register(BidEntity.class);
        register(SignPlayerEntity.class);
        register(TakeOffGeneralEntity.class);
        register(UpdateAuctionParam.class);
        register(UpdateSeasonParam.class);
        register(AuctionSignGeneralParam.class);
        register(BidParam.class);
        register(SyncParam.class);
        register(SignPlayerParam.class);
        register(TakeOffGeneralParam.class);
        register(Types.JAVATYPE_REQUESTLIST);
        register(Types.JAVATYPE_RESPONSELIST);
        register(Types.JAVATYPE_AUCTIONGENERALENTITYLIST);
        register(Types.JAVATYPE_SYNCPARAMLIST);
        register(Boolean.class);
        register(Boolean.TYPE);
        register(Integer.TYPE);
        register(Integer.class);
        register(Date.class);
        register(String.class);
        register(byte[].class);
        register(Byte.TYPE);
        register(NewAuctionSignGeneralParam.class);
        register(NewAuctionGeneralEntity.class);
        register(Types.JAVATYPE_NEWAUCTIONGENERALENTITYLIST);
        register(Types.JAVATYPE_SEASONINFOENTIRYLIST);
        register(SeasonInfoEntity.class);
        register(QueryMatchParam.class);
        register(MatchStateEntity.class);
        register(SignAndSyncParam.class);
        register(SignEntity.class);
        register(QueryMatchScheduleParam.class);
        register(QueryMatchRTInfoParam.class);
        register(MatchRTInfoEntity.class);
        register(QueryMatchReportParam.class);
        register(MatchReportEntity.class);
        register(QueryTurnRankParam.class);
        register(QueryMatchNumScheduleParam.class);
        register(MatchScheduleEntity.class);
        register(Types.JAVATYPE_MATCHSCHEDULEENTIRYLIST);
        register(QueryMatchResultParam.class);
        register(Types.JAVATYPE_CAMPARMYDATALIST);
        register(MatchPlayerEntity.class);
        register(MatchRankEntity.class);
        register(InspireParam.class);
        register(InspireEntity.class);
        register(MatchResultEntity.class);
        register(Types.JAVATYPE_MATCHRANKENTITYLIST);
        register(Types.JAVATYPE_MATCHRESULTENTITYLIST);
        register(GameServerEntity.class);
        register(KfwdSeasonInfo.class);
        register(MatchServerEntity.class);
        register(KfwdMatchScheduleInfo.class);
        register(KfwdRewardResult.class);
        register(KfwdSignInfoParam.class);
        register(KfwdSignResult.class);
        register(KfwdPlayerInfo.class);
        register(KfwdPlayerKey.class);
        register(KfwdScheduleInfoDto.class);
        register(KfwdRTDisPlayInfo.class);
        register(KfwdRTMatchInfo.class);
        register(KfwdState.class);
        register(KfwdSimpleGInfo.class);
        register(KfwdGInfo.class);
        register(Types.JAVATYPE_SIMPLEGENERALINFOLIST);
        register(KfwdDoubleRewardKey.class);
        register(KfwdDoubleRewardResult.class);
        register(KfwdTicketResultInfo.class);
        register(Types.JAVATYPE_KFWDTICKETRESULTINFOLIST);
        register(KfwdGameServerRewardInfo.class);
        register(KfwdTicketMarketListInfo.class);
        register(KfwdTicketMarketInfo.class);
        register(Types.JAVATYPE_KFWDTICKETMARKETINFOLIST);
        register(KfwdRankingRewardInfo.class);
        register(Types.JAVATYPE_KFWDRANKINGREWARDINFO);
        register(KfSpecialGeneral.class);
        register(KfwdDayBattleEndNotice.class);
        register(Types.JAVATYPE_KFWDPLAYERINFO);
        register(KfwdRankTreasureList.class);
        register(KfwdRankTreasureInfo.class);
        register(Types.KFWDRANKTREASUREINFO_LIST);
        register(KfgzSignInfoParam.class);
        register(KfgzPlayerInfo.class);
        register(KfgzSignResult.class);
        register(KfgzSyncDataParam.class);
        register(KfgzSyncDataResult.class);
        register(ResChangeRecord.class);
        register(Types.JAVATYPE_KFGZ_RES_RECORD);
        register(KfgzBattleRewardRes.class);
        register(KfgzEndRewardRes.class);
        register(KfgzNationInfo.class);
        register(KfgzRewardInfoRes.class);
        register(KfgzRewardRes.class);
        register(KfgzRuleInfoList.class);
        register(KfgzRuleInfoRes.class);
        register(KfgzSeasonInfoRes.class);
        register(KfgzScheduleInfoList.class);
        register(KfgzScheduleInfoRes.class);
        register(KfgzBaseInfoRes.class);
        register(KfgzNationResInfo.class);
        register(KfgzPlayerResultInfo.class);
        register(Types.KFGZSCHEDULEINFORES_LIST);
        register(Types.KFGZRULEINFORES_MAP);
        register(Types.KFGZBATTLEREWARDRES_MAP);
        register(Types.KFGZENDREWARDRES_MAP);
        register(Types.KFGZREWARDRES_LIST);
        register(Types.KFGZPLAYERRESULTINFO_LIST);
        register(KfgzGzKey.class);
        register(KfgzNationResKey.class);
        register(KfgzPlayerGzKey.class);
        register(KfgzBattleResultInfo.class);
        register(KfgzNationResultReq.class);
        register(KfgzPlayerRankingInfoReq.class);
        register(KfgzBattleResultRes.class);
        register(Types.KFGZNATIONRESULTREQ_LIST);
        register(Types.KFGZPLAYERRANKINGINFOREQ_LIST);
        register(KfgzAllRankRes.class);
        register(kfgzNationGzKey.class);
        register(MailDto.class);
        register(Types.KFGZMAILDTO_LIST);
        register(KfzbFeastParticipateInfo.class);
        register(KfzbFeastParticipator.class);
        register(KfzbMatchKey.class);
        register(KfzbMatchKeyList.class);
        register(KfzbPhase2MatchKey.class);
        register(KfzbPlayerInfo.class);
        register(KfzbPlayerKey.class);
        register(KfzbPlayerListKey.class);
        register(KfzbRoomKeyList.class);
        register(KfzbRTSupport.class);
        register(KfzbSignInfo.class);
        register(KfzbTopPlayerInfo.class);
        register(KfzbWinnerInfo.class);
        register(FrameBattleReport.class);
        register(KfzbBattleBuffer.class);
        register(KfzbBattleInfo.class);
        register(KfzbBattleReport.class);
        register(KfzbBattleReportList.class);
        register(KfzbFeastInfo.class);
        register(KfzbFeastOrganizer.class);
        register(KfzbFeastPlayerRoomInfo.class);
        register(KfzbMatchInfo.class);
        register(KfzbMatchResDetailInfo.class);
        register(KfzbMatchSupport.class);
        register(KfzbPhase1RewardInfo.class);
        register(KfzbPhase1RewardInfoList.class);
        register(KfzbPhase2Info.class);
        register(KfzbPhase2LayerReward.class);
        register(KfzbPlayerGroup.class);
        register(KfzbPlayerGroupInfo.class);
        register(KfzbPlayerLimit.class);
        register(KfzbPlayerLimitInfo.class);
        register(KfzbRewardInfo.class);
        register(KfzbRoomInfo.class);
        register(KfzbRoomInfoList.class);
        register(KfzbRTMatchInfo.class);
        register(KfzbSeasonInfo.class);
        register(KfzbSignResult.class);
        register(KfzbState.class);
        register(KfzbTreasureReward.class);
        register(Types.KFZBBATTLEINFO_LIST);
        register(Types.KFZBBATTLEINFO_MAP);
        register(Types.KFZBPHASE2MATCHKEY_LIST);
        register(Types.FRAMEBATTLEREPORT_LIST);
        register(Types.KFZBPHASE1REWARDINFO_LIST);
        register(Types.KFZBPLAYERLIMIT_LIST);
        register(Types.KFZBPLAYERGROUP_LIST);
        register(Types.KFZBTOPPLAYERINFO_LIST);
        register(Types.KFZBROOMINFO_LIST);
        register(Types.KFZBFEASTORGANIZER_MAP);
        register(Types.KFZBFEASTPARTICIPATOR_LIST);
        register(Types.KFZBTREASUREREWARD_LIST);
    }
    
    public static synchronized void register(final Class<?> clazz) {
        final JavaType type = TypeFactory.fastSimpleType(clazz);
        final int id = Types.ID.getAndIncrement();
        if (Types.id2JavaType.put(id, type) != null) {
            throw new TypeRegisterException("register type " + clazz.getName() + " duplicate");
        }
        if (Types.javaType2Id.put(type, id) != null) {
            throw new TypeRegisterException("register type " + clazz.getName() + " duplicate");
        }
        if (Types.type2Id.put(clazz, id) != null) {
            throw new TypeRegisterException("register type " + clazz.getName() + " duplicate");
        }
        final ObjectReader objectReader = Types.OBJECT_MAPPER.reader(type);
        if (Types.id2ObjectReader.put(id, objectReader) != null) {
            throw new TypeRegisterException("register type " + clazz.getName() + " duplicate");
        }
        if (Types.javaType2ObjectReader.put(type, objectReader) != null) {
            throw new TypeRegisterException("register type " + clazz.getName() + " duplicate");
        }
        if (Types.clazz2ObjectReader.put(clazz, objectReader) != null) {
            throw new TypeRegisterException("register type " + clazz.getName() + " duplicate");
        }
    }
    
    public static synchronized void register(final JavaType type) {
        final int id = Types.ID.getAndIncrement();
        if (Types.id2JavaType.put(id, type) != null) {
            throw new TypeRegisterException("register type " + type.getGenericSignature() + " duplicate");
        }
        if (Types.javaType2Id.put(type, id) != null) {
            throw new TypeRegisterException("register type " + type.getGenericSignature() + " duplicate");
        }
        final ObjectReader objectReader = Types.OBJECT_MAPPER.reader(type);
        if (Types.id2ObjectReader.put(id, objectReader) != null) {
            throw new TypeRegisterException("register type " + type.getGenericSignature() + " duplicate");
        }
        if (Types.javaType2ObjectReader.put(type, objectReader) != null) {
            throw new TypeRegisterException("register type " + type.getGenericSignature() + " duplicate");
        }
    }
    
    public static final JavaType javaType(final int id) {
        return Types.id2JavaType.get(id);
    }
    
    public static final int id(final Class<?> clazz) {
        if (Types.type2Id.get(clazz) == null) {
            System.out.println(clazz.getName());
        }
        return Types.type2Id.get(clazz);
    }
    
    public static final int id(final JavaType type) {
        return Types.javaType2Id.get(type);
    }
    
    public static final ObjectReader objectReader(final Class<?> clazz) {
        return Types.clazz2ObjectReader.get(clazz);
    }
    
    public static final ObjectReader objectReader(final JavaType type) {
        return Types.javaType2ObjectReader.get(type);
    }
    
    public static final ObjectReader objectReader(final int id) {
        return Types.id2ObjectReader.get(id);
    }
    
    public static void main(final String[] args) throws JsonGenerationException, JsonMappingException, IOException {
        final List<Response> responseList = new ArrayList<Response>();
        final Response response = new Response();
        response.setResponseId(1);
        response.setCommand(22);
        final KfgzBaseInfoRes res = new KfgzBaseInfoRes();
        res.addMail(new MailDto(1, "231", "444", 1));
        response.setMessage(res);
        responseList.add(response);
        final byte[] bodyBytes = Types.OBJECT_MAPPER.writeValueAsBytes(responseList);
        List<Response> responses = null;
        responses = (List<Response>)objectReader(Types.JAVATYPE_RESPONSELIST).readValue(bodyBytes);
        System.out.println(responses.size());
    }
}
