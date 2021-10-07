package com.app.findyourlobster.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.location.LocationListener
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import com.app.findyourlobster.R
import com.app.findyourlobster.data.*
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.*
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.hanks.htextview.typer.TyperTextView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.random.Random


class HomeFragment : Fragment(), CardStackListener, LocationListener {
    var spots: ArrayList<Any>? = null
    var ads: ArrayList<UnifiedNativeAd>? = null
    var auth: FirebaseAuth? = null
    var currentUser: FirebaseUser? = null
    var database: FirebaseDatabase? = null
    var myRef: DatabaseReference? = null
    var emailUpdated: String? = null
    var value: String? = null
    var saveCurrentDate: String? = null
    var saveCurrentTime: String? = null
    private var loadingLayout: RelativeLayout? = null
    private var findButtonLayout: RelativeLayout? = null
    private var cardStackView: CardStackView? = null
    private var adapter: CardStackAdapter? = null
    private var skip: View? = null
    private var rewind: View? = null
    private var findMore: Button? = null
    private var like: View? = null
    private var super_like: View? = null
    private var textView: TyperTextView? = null
    private val manager by lazy { CardStackLayoutManager(activity, this) }
    var mMediaPlayer: MediaPlayer? = null
    var sharedPreferences: SharedPreferences? = null
    var age: Boolean = false
    var loc: Boolean = false
    var gen: Boolean = false
    var myTopPostsQuery: Query? = null
    var swipedUserEmail: String? = null
    var appearedUserEmail: String? = null
    var provider: Boolean = true
    var template: TemplateView? = null

    @SuppressLint("CommitPrefEdits")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        cardStackView = root.findViewById(R.id.card_stack_view)
        skip = root.findViewById<View>(R.id.skip_button)
        rewind = root.findViewById<View>(R.id.rewind_button)
        like = root.findViewById<View>(R.id.like_button)
        super_like = root.findViewById<View>(R.id.super_button)
        findMore = root.findViewById<Button>(R.id.find)
        template = root.findViewById(R.id.my_template)
        loadingLayout = root.findViewById(R.id.loadingLayout)
        findButtonLayout = root.findViewById(R.id.buttonLoadLayout)
        textView = root.findViewById(R.id.subscribe_text)

        myRef = FirebaseDatabase.getInstance().getReference("AllUsersData")

        spots = ArrayList()
        ads = ArrayList()
        sharedPreferences = context?.getSharedPreferences("PREFS", 0)
        val editor = sharedPreferences!!.edit()



        if (context?.let { it1 -> ActivityCompat.checkSelfPermission(it1, Manifest.permission.ACCESS_FINE_LOCATION) } != PackageManager.PERMISSION_GRANTED && context?.let { it1 -> ActivityCompat.checkSelfPermission(it1, Manifest.permission.ACCESS_COARSE_LOCATION) } != PackageManager.PERMISSION_GRANTED) {
            val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            requestPermissions(permissions, PackageManager.PERMISSION_GRANTED)
        } else {
            loc = sharedPreferences!!.getBoolean("locationFilter", false)
            age = sharedPreferences!!.getBoolean("ageFilter", false)
            gen = sharedPreferences!!.getBoolean("genderFilter", false)
            sharedPreferences = requireContext().getSharedPreferences("PREFS", 0)

            if (gen) {

                if (sharedPreferences!!.getBoolean("male", false)) {

                    myTopPostsQuery = myRef!!.orderByChild("gender").equalTo("male")
                } else if (sharedPreferences!!.getBoolean("female", false)) {
                    myTopPostsQuery = myRef!!.orderByChild("gender").equalTo("female")

                } else {
                    myTopPostsQuery = myRef!!
                }
            } else if (age) {
                myTopPostsQuery = myRef!!.orderByChild("age").startAt(sharedPreferences!!.getInt("ageValue1", 16).toDouble()).endAt(sharedPreferences!!.getInt("ageValue2", 100).toDouble())
            } else if (loc) {

                myTopPostsQuery = myRef!!.orderByChild("location").startAt(encode(sharedPreferences!!.getFloat("latt", 0f).toDouble(), sharedPreferences!!.getFloat("longit", 0f).toDouble(), 5))
            } else {
                myTopPostsQuery = myRef!!
            }
            findButtonLayout!!.visibility = View.INVISIBLE
            //loadingLayout!!.visibility = View.VISIBLE
            // spots!!.clear()
            myTopPostsQuery!!.limitToFirst(6).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dsnapshot: DataSnapshot) {
                    var image = ""
                    var description = ""
                    for (dataSnapshot in dsnapshot.getChildren()) {
                        val email = dataSnapshot.key!!
                        Log.i("Entered", "again")
                        var age: String
                        if (dataSnapshot.child("userInfo").child("age").exists())
                            age = dataSnapshot.child("userInfo").child("age").children.iterator().next().value.toString()
                        else {
                            age = " "
                        }
                        val name = dataSnapshot.child("userInfo").child("name").children.iterator().next().value.toString()

                        if (dataSnapshot.child("starred").exists()) {
                            image = dataSnapshot.child("starred").children.iterator().next().value.toString()
                        } else {
                            image = ""
                        }
                        if (dataSnapshot.child("userInfo").child("description").exists()) {
                            description = dataSnapshot.child("userInfo").child("description").children.iterator().next().value.toString()
                        }
                        spots!!.add(Spot(name = name + ", " + age, description = description, url = image, email = email))
                        loadingLayout!!.visibility = View.INVISIBLE



                        adapter!!.notifyItemInserted(spots!!.size - 1)

                    }

                }

                override fun onCancelled(error: DatabaseError) {}
            })

            lateinit var adLoader: AdLoader
            adLoader = AdLoader.Builder(context, "ca-app-pub-4849332082663766/7176610228")
                    .forUnifiedNativeAd { ad: UnifiedNativeAd ->

                        val styles = NativeTemplateStyle.Builder().withMainBackgroundColor(ColorDrawable(573371)).build()
                        template!!.setStyles(styles)
                        template!!.setNativeAd(ad)
                        if (!adLoader.isLoading) {
                            Log.i("Loaded", "Loaded")


                            spots!!.add(ad)
                            adapter!!.notifyItemInserted(spots!!.size - 1)
                            //adapter!!.notifyDataSetChanged()

                            // adapter!!.notifyDataSetChanged()
                            // The AdLoader is still loading ads.
                            // Expect more adLoaded or onAdFailedToLoad callbacks.
                        }

                    }
                    .withAdListener(object : AdListener() {
                        override fun onAdFailedToLoad(adError: LoadAdError) {
                            // Handle the failure by logging, altering the UI, and so on.
                            Log.d("mAdView", "onAdFailedToLoad. But why? " + adError.cause)

                        }
                    })
                    .withNativeAdOptions(NativeAdOptions.Builder()
                            // Methods in the NativeAdOptions.Builder class can be
                            // used here to specify individual options settings.
                            .build())
                    .build()
            adLoader.loadAd(AdRequest.Builder().build())

        }
        adapter = CardStackAdapter(spots!!)
        setupCardStackView()
        setupButton()

        auth = FirebaseAuth.getInstance()


        currentUser = auth!!.currentUser
        loadingLayout = root.findViewById(R.id.loadingLayoutFragmentHome)
        emailUpdated = if (currentUser!!.email!!.contains(".")) {
            currentUser!!.email!!.replace(".", " ")
        } else {
            currentUser!!.email
        }

        //loadingLayout.setVisibility(View.VISIBLE)


        return root
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onCardDragging(direction: Direction, ratio: Float) {
        Log.d("CardStackView", "onCardDragging: d = ${direction.name}, r = $ratio")
    }

    fun rand(start: Int, end: Int): Int {
        require(start <= end) { "Illegal Argument" }
        return Random(System.nanoTime()).nextInt(start, end + 1)
    }

    override fun onCardSwiped(direction: Direction?) {
        if (manager!!.topPosition == adapter!!.itemCount) {
            findButtonLayout!!.visibility = View.VISIBLE


        } else {
            findButtonLayout!!.visibility = View.INVISIBLE

        }

        if (manager!!.topPosition == adapter!!.itemCount - 2) {
            paginate()
        }


    }

    private fun paginate() {
        val newList: ArrayList<Any> = ArrayList()
        loc = sharedPreferences!!.getBoolean("locationFilter", false)
        age = sharedPreferences!!.getBoolean("ageFilter", false)
        gen = sharedPreferences!!.getBoolean("genderFilter", false)
        sharedPreferences = requireContext().getSharedPreferences("PREFS", 0)
        //MobileAds.initialize(context) {}
        Log.i("paginated", "Paginated");
        if (gen) {

            if (sharedPreferences!!.getBoolean("male", false)) {

                myTopPostsQuery = myRef!!.orderByChild("gender").equalTo("male")
            } else if (sharedPreferences!!.getBoolean("female", false)) {
                myTopPostsQuery = myRef!!.orderByChild("gender").equalTo("female")

            } else {
                myTopPostsQuery = myRef!!
            }
        } else if (age) {
            myTopPostsQuery = myRef!!.orderByChild("age").startAt(sharedPreferences!!.getInt("ageValue1", 16).toDouble()).endAt(sharedPreferences!!.getInt("ageValue2", 100).toDouble())
        } else if (loc) {

            myTopPostsQuery = myRef!!.orderByChild("location").startAt(encode(sharedPreferences!!.getFloat("latt", 0f).toDouble(), sharedPreferences!!.getFloat("longit", 0f).toDouble(), 5))
        } else {
            myTopPostsQuery = myRef!!
        }
        findButtonLayout!!.visibility = View.INVISIBLE
        //loadingLayout!!.visibility = View.VISIBLE
        myTopPostsQuery!!.limitToFirst(6).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dsnapshot: DataSnapshot) {
                var image: String
                var description = ""
                for (dataSnapshot in dsnapshot.getChildren()) {
                    val email = dataSnapshot.key!!
                    Log.i("Entered", "again")
                    var age: String
                    if (dataSnapshot.child("userInfo").child("age").exists())
                        age = dataSnapshot.child("userInfo").child("age").children.iterator().next().value.toString()
                    else {
                        age = " "
                    }
                    val name = dataSnapshot.child("userInfo").child("name").children.iterator().next().value.toString()
                    if (dataSnapshot.child("starred").exists()) {
                        image = dataSnapshot.child("starred").children.iterator().next().value.toString()
                    } else {
                        image = ""
                    }
                    if (dataSnapshot.child("userInfo").child("description").exists()) {
                        description = dataSnapshot.child("userInfo").child("description").children.iterator().next().value.toString()
                    }
                    spots!!.add(Spot(name = name + ", " + age, description = description, url = image, email = email))

                    loadingLayout!!.visibility = View.INVISIBLE
                    adapter!!.notifyItemInserted(spots!!.size - 1)

                }
                lateinit var adLoader: AdLoader
                adLoader = AdLoader.Builder(context, "ca-app-pub-4849332082663766/7176610228")
                        .forUnifiedNativeAd { ad: UnifiedNativeAd ->

                            val styles = NativeTemplateStyle.Builder().withMainBackgroundColor(ColorDrawable(573371)).build()
                            template!!.setStyles(styles)
                            template!!.setNativeAd(ad)
                            if (!adLoader.isLoading) {
                                Log.i("Loaded", "Loaded")


                                spots!!.add(ad)
                                adapter!!.notifyItemInserted(spots!!.size - 1)

                                // adapter!!.notifyDataSetChanged()
                                // The AdLoader is still loading ads.
                                // Expect more adLoaded or onAdFailedToLoad callbacks.
                            }

                        }
                        .withAdListener(object : AdListener() {
                            override fun onAdFailedToLoad(adError: LoadAdError) {
                                // Handle the failure by logging, altering the UI, and so on.
                                Log.d("mAdView", "onAdFailedToLoad. But why? " + adError.cause)

                            }
                        })
                        .withNativeAdOptions(NativeAdOptions.Builder()
                                // Methods in the NativeAdOptions.Builder class can be
                                // used here to specify individual options settings.
                                .build())
                        .build()
                adLoader.loadAd(AdRequest.Builder().build())


            }

            override fun onCancelled(error: DatabaseError) {}
        })

        ///adapter!!.setData(newList)
        //result.dispatchUpdatesTo(adapter!!)
    }

    override fun onCardRewound() {
        Log.d("CardStackView", "onCardRewound: ${manager.topPosition}")
        findButtonLayout!!.visibility = View.INVISIBLE

    }

    override fun onCardCanceled() {
        Log.d("CardStackView", "onCardCanceled: ${manager.topPosition}")
    }

    override fun onCardAppeared(view: View, position: Int) {
        val textView = view.findViewById<TextView>(R.id.email)
//        Log.d("CardStackView", "onCardAppeared: ($position) ${textView.text}")
        if (textView != null) {
            appearedUserEmail = textView.text as String?
        }
    }

    override fun onCardDisappeared(view: View, position: Int) {
        val textView = view.findViewById<TextView>(R.id.email)
        if (textView != null) {
            swipedUserEmail = textView.text as String?
            Log.d("CardStackView", "onCardDisappeared: ($position) ${textView.text}")

        }
    }

    private fun setupCardStackView() {
        initialize()
    }
    fun Fragment.vibratePhone() {
        val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(200)
        }
    }
    @SuppressLint("ObjectAnimatorBinding")
    private fun setupButton() {
        skip?.setOnClickListener {
            textView!!.visibility = GONE
            val setting = SwipeAnimationSetting.Builder()
                    .setDirection(Direction.Left)
                    .setDuration(Duration.Normal.duration)
                    .setInterpolator(AccelerateInterpolator())
                    .build()
            manager.setSwipeAnimationSetting(setting)
            cardStackView?.swipe()
        }

        rewind?.setOnClickListener {
            if(true){
                vibratePhone()
                textView!!.visibility = VISIBLE
                textView!!.animateText("subscribe to the premium version to use this button freely");

            }else {

                val setting = RewindAnimationSetting.Builder()
                    .setDirection(Direction.Bottom)
                    .setDuration(Duration.Normal.duration)
                    .setInterpolator(DecelerateInterpolator())
                    .build()
                manager.setRewindAnimationSetting(setting)
                cardStackView?.rewind()
            }
        }

        like?.setOnClickListener {
            textView!!.visibility = GONE

            val setting = SwipeAnimationSetting.Builder()
                    .setDirection(Direction.Right)
                    .setDuration(Duration.Normal.duration)
                    .setInterpolator(AccelerateInterpolator())
                    .build()
            manager.setSwipeAnimationSetting(setting)
            cardStackView?.swipe()
            val myRef = FirebaseDatabase.getInstance().getReference("SwipingData").child(emailUpdated!!)


            if (swipedUserEmail != null) {
                myRef.child(swipedUserEmail!!).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.value == null) {
                            val likedMap = HashMap<String, String>()

                            Log.i("not existing", "none")
                            likedMap["liked"] = swipedUserEmail!!
                            myRef.child(swipedUserEmail!!).updateChildren(likedMap as Map<String, Any>)
                            val myRef2 = FirebaseDatabase.getInstance().getReference("SwipingData").child(swipedUserEmail!!)
                            val LikedByMap = HashMap<String, String>()
                            LikedByMap["likedby"] = emailUpdated!!
                            myRef2.child(emailUpdated!!).updateChildren(LikedByMap as Map<String, Any>)
                        } else {
                            if (snapshot.child("liked").exists()) {
                                Log.i("exists", "likedexists")
                            } else if (snapshot.child("likedby").exists()) {
                                if (snapshot.child("likedby").value!! == swipedUserEmail) {
                                    Log.i("exists", "likedbyexists")
                                    sendMessage()
                                    myRef.child(swipedUserEmail!!).removeValue()
                                    val myRef2 = FirebaseDatabase.getInstance().getReference("SwipingData").child(swipedUserEmail!!)
                                    myRef2.child(emailUpdated!!).removeValue()
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
            }
        }
        super_like?.setOnClickListener {
            if(true){
                vibratePhone()
                textView!!.visibility = VISIBLE
                textView!!.animateText("subscribe to the premium version to use this button freely");

            }else {
                val textView = requireView().findViewById<TextView>(R.id.email)

                if (textView != null) {
                    mMediaPlayer = MediaPlayer.create(activity, R.raw.oh_my_god)
                    mMediaPlayer!!.isLooping = false
                    mMediaPlayer!!.start()

                }
                val setting = SwipeAnimationSetting.Builder()
                    .setDirection(Direction.Top)
                    .setDuration(Duration.Slow.duration)
                    .setInterpolator(AccelerateInterpolator())
                    .build()
                manager.setSwipeAnimationSetting(setting)
                cardStackView?.swipe()
                myRef = FirebaseDatabase.getInstance().getReference("Requests")
                val Map: MutableMap<String, Any> = HashMap()
                Map?.set("status", "pending")

                myRef!!.child(swipedUserEmail!!).child(emailUpdated!!).updateChildren(Map!!)
            }
        }
    }

    private fun initialize() {
        manager.setStackFrom(StackFrom.None)
        manager.setVisibleCount(3)
        manager.setTranslationInterval(8.0f)
        manager.setScaleInterval(0.95f)
        manager.setSwipeThreshold(0.3f)
        manager.setMaxDegree(20.0f)
        manager.setDirections(Direction.HORIZONTAL)
        manager.setCanScrollHorizontal(true)
        manager.setCanScrollVertical(true)
        manager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
        manager.setOverlayInterpolator(LinearInterpolator())
        cardStackView!!.layoutManager = manager
        cardStackView!!.adapter = adapter
        cardStackView!!.itemAnimator.apply {
            if (this is DefaultItemAnimator) {
                supportsChangeAnimations = false
            }
        }
    }


    private fun createSpot(): Spot {
        return Spot(
                name = "Yasaka Shrine",
                description = "Kyoto",
                url = "https://source.unsplash.com/Xq1ntWruZQI/600x800",
                email = "smoheb33@gmail com"
        )
    }

    private fun createSpots(): ArrayList<Spot> {
        val spots = ArrayList<Spot>()
        val myRef = FirebaseDatabase.getInstance().getReference("AllUsersData")


        spots.add(Spot(name = "Fushimi Inari Shrine", description = "Kyoto", url = "https://source.unsplash.com/NYyCqdBOKwc/600x800", email = "smoheb33@gmail com"))
        spots.add(Spot(name = "Bamboo Forest", description = "Kyoto", url = "https://source.unsplash.com/buF62ewDLcQ/600x800", email = "smoheb33@gmail com"))
        spots.add(Spot(name = "Brooklyn Bridge", description = "New York", url = "https://source.unsplash.com/THozNzxEP3g/600x800", email = "smoheb33@gmail com"))
        spots.add(Spot(name = "Empire State Building", description = "New York", url = "https://source.unsplash.com/USrZRcRS2Lw/600x800", email = "smoheb33@gmail com"))
        spots.add(Spot(name = "Empire State Building", description = "New York", url = "https://source.unsplash.com/USrZRcRS2Lw/600x800", email = "smoheb33@gmail com"))


        return spots
    }

    fun sendMessage() {
        try {
            var root: DatabaseReference? = null
            root = FirebaseDatabase.getInstance().reference

            val message: String = "How you doin'"
            val messagesender_ref = "Chats/$emailUpdated/$swipedUserEmail"
            val messagereceiver_ref = "Chats/$swipedUserEmail/$emailUpdated"
            val calForDate = Calendar.getInstance()
            val currentDate = SimpleDateFormat("dd-MM-yyyy")
            saveCurrentDate = currentDate.format(calForDate.time)
            val calForTime = Calendar.getInstance()
            val currentTime = SimpleDateFormat("HH:mm:ss")
            saveCurrentTime = currentTime.format(calForTime.time)
            val databaseReference: DatabaseReference = root.child("Chats").child(emailUpdated!!).child(swipedUserEmail!!).push()
            val message_push_id = databaseReference.key
            val hashMap: MutableMap<String, String> = HashMap()
            hashMap["sender"] = emailUpdated!!
            hashMap["message"] = message
            hashMap["to"] = swipedUserEmail!!
            hashMap["date"] = saveCurrentDate!!
            hashMap["time"] = saveCurrentTime!!
            hashMap["type"] = "text"
            val hashMap2: MutableMap<String, HashMap<String, String>> = HashMap()
            hashMap2["$messagesender_ref/$message_push_id"] = hashMap as HashMap<String, String>
            hashMap2["$messagereceiver_ref/$message_push_id"] = hashMap
            root.updateChildren(hashMap2 as Map<String, Any>)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        @JvmStatic
        fun encode(latitude: Double, longitude: Double, precision: Int): String {
            val characterMap = charArrayOf(
                    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
                    'j', 'k', 'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
            )
            val latitudeBits = getBits(latitude, -90.0, 90.0, precision)
            val longitudeBits = getBits(longitude, -180.0, 180.0, precision)
            val mixLists = longitudeBits.mapIndexed { i, b -> listOf(b, latitudeBits[i]) }.flatten()
            val chars = mixLists.batch(5).map { it.toBooleanArray().toInt() }.map { characterMap[it] }
            return String(chars.toCharArray())
        }

        fun getBits(value: Double, low: Double, high: Double, precision: Int): BooleanArray {
            val length = (precision * 5) / 2
            var currentLow = low
            var currentHigh = high
            return (0..length - 1)
                    .map {
                        val division = (currentLow + currentHigh) / 2
                        val current = (value >= division)
                        if (current) currentLow = division
                        else currentHigh = division
                        current
                    }
                    .toBooleanArray()
        }


        fun BooleanArray.toInt(): Int {
            var n: Int = 0
            this.forEach { n = (n shl 1) or if (it) 1 else 0 }
            return n
        }


        public fun <T> Iterable<T>.batch(chunkSize: Int): List<List<T>> {
            return mapIndexed { i, item -> i to item }
                    .groupBy { it.first / chunkSize }
                    .map { it.value.map { it.second } }
        }

    }

    override fun onLocationChanged(location: Location) {
        TODO("Not yet implemented")
    }

    override fun onProviderDisabled(provider: String) {
        this@HomeFragment.provider = false
    }

    override fun onProviderEnabled(provider: String) {
        this@HomeFragment.provider = true
    }


}




