package com.example.newsreader.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.newsreader.R
import com.example.newsreader.adapter.ArticleAdapter
import com.example.newsreader.localdb.DatabaseHelper
import com.example.newsreader.model.Article
import com.example.newsreader.model.PreferenceM
import com.example.newsreader.model.TopHeadlines
import com.example.newsreader.news_api.TopHeadlinesEndpoint
import com.example.newsreader.ui.profile.ProfileFragment
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.fragment_home.empty_text
import kotlinx.android.synthetic.main.fragment_home.retry_fetch_button
import kotlinx.android.synthetic.main.fragment_home.swipe_refresh
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DashboardFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener  {


    private val ENDPOINT_URL by lazy { "https://newsapi.org/v2/" }
    private lateinit var topHeadlinesEndpoint: TopHeadlinesEndpoint
    private lateinit var newsApiConfig: String
    private lateinit var articleAdapter: ArticleAdapter
    private lateinit var articleList: ArrayList<Article>
    private lateinit var userKeyWordInput: String
    // RxJava related fields
    private lateinit var topHeadlinesObservable: Observable<TopHeadlines>
    private lateinit var compositeDisposable: CompositeDisposable

    companion object {
        lateinit var dbHandler: DatabaseHelper
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        networkInit()
        dbHandler = DatabaseHelper(activity!!.applicationContext, null, null, 1)
        preferencesInit()

        swipe_refresh.setOnRefreshListener {
            queryTopHeadlines()
            swipe_refresh.isRefreshing = false
        }

    }

    private fun preferencesInit(){
        //load the preferences list
        val prefList = dbHandler.getAllPrefernces(activity!!.applicationContext)
        val spinner = spinner_preferences

        val preferences = prefList.toTypedArray()//arrayOfNulls<String>(prefList.size)
//        prefList.toArray()

        val arrayAdapter = ArrayAdapter(activity!!.applicationContext, android.R.layout.simple_spinner_dropdown_item, preferences)
        spinner.adapter = arrayAdapter
//        spinner.setOnClickListener {
                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View,
                        position: Int,
                        id: Long
                    ) {
                        if (preferences.isEmpty() || preferences.size == 0){
                            //do nothing
                        }else{
                            Toast.makeText(
                                activity!!.applicationContext,"preference selected : " +  preferences[position],Toast.LENGTH_SHORT).show()
                            checkQueryText(preferences[position].toString())
                        }
                    }
                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // Code to perform some action when nothing is selected
                        queryTopHeadlines()
                    }
                }
//            }
    }
    private fun networkInit(){
        //Network request
        val retrofit: Retrofit = generateRetrofitBuilder()
        topHeadlinesEndpoint = retrofit.create(TopHeadlinesEndpoint::class.java)
        newsApiConfig = resources.getString(R.string.api_key)
        swipe_refresh.setOnRefreshListener(this)
        swipe_refresh.setColorSchemeResources(R.color.colorAccent)
        articleList = ArrayList()
        articleAdapter = ArticleAdapter(context!!, articleList)

        userKeyWordInput = ""

        compositeDisposable = CompositeDisposable()
        recycler_viewFilter.setHasFixedSize(true)
        recycler_viewFilter.layoutManager = LinearLayoutManager(context)
        recycler_viewFilter.itemAnimator = DefaultItemAnimator()
        recycler_viewFilter.adapter = articleAdapter
    }

    override fun onStart() {
        super.onStart()
        queryTopHeadlines()
    }
    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
    override fun onRefresh() {
        queryTopHeadlines()
    }
    private fun checkUserKeywordInput() {
        if (userKeyWordInput.isEmpty()) {
            queryTopHeadlines()
        } else {
            getKeyWordQuery(userKeyWordInput)
        }
    }
    private fun checkQueryText(userInput: String?): Boolean {
        if (userInput != null && userInput.length > 1) {
            userKeyWordInput = userInput
            getKeyWordQuery(userInput)
        } else if (userInput != null && userInput == "") {
            userKeyWordInput = ""
            queryTopHeadlines()
        }
        return false
    }


    private fun getKeyWordQuery(userKeywordInput: String) {
        swipe_refresh.isRefreshing = true
        if (userKeywordInput != null && userKeywordInput.isNotEmpty()) {
            topHeadlinesObservable = topHeadlinesEndpoint.getUserSearchInput(newsApiConfig, userKeywordInput)
            subscribeObservableOfArticle()
        } else {
            queryTopHeadlines()
        }
    }
    private fun queryTopHeadlines() {
        swipe_refresh.isRefreshing = true
        topHeadlinesObservable = topHeadlinesEndpoint.getTopHeadlines("us", newsApiConfig)
        subscribeObservableOfArticle()
    }

    private fun subscribeObservableOfArticle() {
        articleList.clear()
        compositeDisposable.add(
            topHeadlinesObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap {
                    Observable.fromIterable(it.articles)
                }
                .subscribeWith(createArticleObserver())
        )
    }

    private fun createArticleObserver(): DisposableObserver<Article> {
        return object : DisposableObserver<Article>() {
            override fun onNext(article: Article) {
                if (!articleList.contains(article)) {
                    articleList.add(article)
                }
            }

            override fun onComplete() {
                showArticlesOnRecyclerView()
            }

            override fun onError(e: Throwable) {
                Log.e("createArticleObserver", "Article error: ${e.message}")
            }
        }
    }

    private fun showArticlesOnRecyclerView() {
        if (articleList.size > 0) {
            empty_text.visibility = View.GONE
            retry_fetch_button.visibility = View.GONE
            recycler_viewFilter.visibility = View.VISIBLE
            articleAdapter.setArticles(articleList)
        } else {
            recycler_viewFilter.visibility = View.GONE
            empty_text.visibility = View.VISIBLE
            retry_fetch_button.visibility = View.VISIBLE
            retry_fetch_button.setOnClickListener { checkUserKeywordInput() }
        }
        swipe_refresh.isRefreshing = false
    }

    private fun generateRetrofitBuilder(): Retrofit {

        return Retrofit.Builder()
            .baseUrl(ENDPOINT_URL)
            .addConverterFactory(GsonConverterFactory.create())
            //Add RxJava2CallAdapterFactory as a Call adapter when building your Retrofit instance
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }
}