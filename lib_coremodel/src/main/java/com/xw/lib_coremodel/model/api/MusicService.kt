package com.xw.lib_coremodel.model.api

import com.xw.lib_coremodel.model.bean.*
import com.xw.lib_coremodel.model.bean.home.*
import com.xw.lib_coremodel.model.bean.login.EmailLoginResponse
import com.xw.lib_coremodel.model.bean.login.LoginResponse
import com.xw.lib_coremodel.model.bean.login.PhoneExist
import com.xw.lib_coremodel.model.bean.search.*
import com.xw.lib_coremodel.model.bean.video.VideoListResponse
import com.xw.lib_coremodel.model.bean.video.VideoTypeResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
interface MusicService {
    companion object {
        const val BASE_URL = "http://192.168.1.2:3000"
//        const val BASE_URL = "http://172.20.10.6:3000"
//        const val BASE_URL = "http://192.168.16.15:3000"
//        const val BASE_URL = "http://194.168.1.102:3000"
    }

    @GET("/banner")
    suspend fun getBanner(@Query("type") type: String): Banners

    @GET("/personalized") //推荐歌单 不需要登陆
    suspend fun getHomeRecommendPlayList(@Query("limit") limit: Int): RecommendPlayList

    @GET("/recommend/resource")
    suspend fun getRecommendPlayListNeedLogin(@Query("timestamp") timestamp: String = System.currentTimeMillis().toString()): RecommendPlayList

    @GET("/top/album")
    suspend fun getHomeRecommendAlbum(@Query("offset") offset: Int, @Query("limit") limit: Int): AlbumListData

    @GET("/personalized/newsong") //新歌推荐
    suspend fun getRecommendNewSong(): NewSongData

    @GET("/toplist/detail")
    suspend fun getTopList(): TopList

    @GET("/playlist/detail")
    suspend fun getPlayList(@Query("id") id: String, @Query("timestamp") timestamp: String): PlayListData

    @GET("/song/url")
    suspend fun getPlayUrl(@Query("id") ids: String): PlayUrlData

    @GET("/lyric")
    suspend fun getLrc(@Query("id") ids: String): SongLrc

    @GET("/cellphone/existence/check")
    suspend fun checkPhone(@Query("phone") phone: String): PhoneExist

    @FormUrlEncoded
    @POST("/captcha/sent")
    suspend fun sendCaptcha(@Field("phone") phone: String): BaseHttpResponse

    @FormUrlEncoded
    @POST("/captcha/verify")
    suspend fun verifyCaptcha(@Field("phone") phone: String, @Field("captcha") captcha: String): BaseHttpResponse

    @FormUrlEncoded
    @POST("/login/cellphone")
    suspend fun phoneLogin(@Field("phone") phone: String, @Field("password") password: String): LoginResponse

    @FormUrlEncoded
    @POST("/login")
    suspend fun emailLogin(@Field("email") email: String, @Field("password") password: String): EmailLoginResponse

    @FormUrlEncoded
    @POST("/register/cellphone")
    suspend fun registerWithPhone(
        @Field("phone") phone: String, @Field("password") password: String, @Field(
            "captcha"
        ) captcha: String, @Field("nickname") nickname: String?
    ): LoginResponse

    @FormUrlEncoded
    @POST("/likelist")
    suspend fun getLikeIds(@Field("timestamp") timestamp: String = System.currentTimeMillis().toString()): LikeListResponse

    @FormUrlEncoded
    @POST("/like")
    suspend fun likeMusic(
        @Field("id") id: String, @Field("like") isLike: Boolean,
        @Field("timestamp") timestamp: String = System.currentTimeMillis().toString()
    ): BaseHttpResponse

    @GET("/recommend/songs")
    suspend fun getRecdDaily(): RecdDailyData

    @GET("/playlist/subscribe")
    suspend fun subscribePlayList(
        @Query("id") id: String, @Query("t") t: Int,
        @Query("timestamp") timestamp: String = System.currentTimeMillis().toString()
    ): BaseHttpResponse

    /**
     * 歌曲信息 多个id用逗号分割
     */
    @FormUrlEncoded
    @POST("/song/detail")
    suspend fun getSongDetail(@Field("ids") ids: String): SongDetailResponse

    /**
     * 歌单全部分类
     */
    @GET("/playlist/catlist")
    suspend fun getPlaylistCatList(): PlayListCatListResponse

    /**
     * 热门歌单分类
     */
    @GET("/playlist/hot")
    suspend fun getPlaylistHot(): PlayListHotResponse

    /**
     * 获取歌单列表
     * order 可选值为 'new' 和 'hot', 分别对应最新和最热 , 默认为 'hot'
     * cat  比如 " 华语 "、" 古风 " 、" 欧美 "、" 流行 ", 默认为 "全部"
     * limit 数量
     */
    @GET("/top/playlist")
    suspend fun getTopPlaylist(
        @Query("order") order: String = "hot",
        @Query("cat") cat: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int = -1
    ): TopPlayListsRespose

    /**
     * 精品歌单
     * cat  比如 " 华语 "、" 古风 " 、" 欧美 "、" 流行 ", 默认为 "全部"
     */
    @GET("/top/playlist/highquality")
    suspend fun getPlaylistHighquality(
        @Query("cat") cat: String = "全部",
        @Query("limit") limit: Int,
        @Query("before") before: String = ""
    ): TopPlayListsRespose

    /**
     * 默认搜索关键词
     */
    @GET("/search/default")
    suspend fun getDefaultSearch(@Query("timestamp") timestamp: String = System.currentTimeMillis().toString()): DefaultSearchResponse

    /**
     * 热门搜索列表
     */
    @GET("/search/hot/detail")
    suspend fun getHotSearchDetail(): HotSearchResponse

    /**
     * 搜索建议
     */
    @GET("/search/suggest")
    suspend fun getSuggestSearch(
        @Query("keywords") keywords: String,
        @Query("type") type: String = "mobile"
    ): SuggestSearchResponse

    /**
     * type: 搜索类型；默认为 1 即单曲 , 取值意义 : 1: 单曲, 10: 专辑, 100: 歌手, 1000: 歌单, 1002: 用户, 1004: MV, 1006: 歌词, 1009: 电台, 1014: 视频
     */
    @GET("/search")
    suspend fun search(
        @Query("keywords") keywords: String,
        @Query("type") type: Int = 1,
        @Query("limit") limit: Int = 30,
        @Query("offset") offset: Int = 0
    ): SearchResponse

    @GET("/search")
    fun searchResult(
        @Query("keywords") keywords: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
        @Query("type") type: Int
    ): Call<ResponseBody>

    @GET("/search")
    suspend fun searchComposite(
        @Query("keywords") keywords: String,
        @Query("type") type: Int = 1018
    ): SearchCompositeResponse

    @GET("/video/group/list")
    suspend fun getVideoType(): VideoTypeResponse

    @GET("/video/group")
    fun getVideoList(
        @Query("id") id: String,
        @Query("timestamp") timestamp: String = System.currentTimeMillis().toString()
    ): Call<VideoListResponse>
}