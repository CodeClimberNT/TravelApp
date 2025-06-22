package com.example.final_assignment_even_g28.data

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.memoryCacheSettings
import com.google.firebase.firestore.persistentCacheSettings
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage

private object Secrets {
    const val SUPABASE_PROJECT_URL = "https://zmloywtvzhxhbqrszvez.supabase.co"
    const val SUPABASE_ANON_KEY =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InptbG95d3R2emh4aGJxcnN6dmV6Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDkyMDQ5NDMsImV4cCI6MjA2NDc4MDk0M30.wMC38KQ2gmN_PdTZEcQt5Z4rYso3IrnhfY0bojRvznk"
}

object Collections {
    private const val C_TRAVEL_PROPOSALS = "travel_proposals"
    private const val C_TRAVEL_REVIEW = "travel_reviews"
    private const val C_USERS = "users"
    private const val C_USER_REVIEW = "user_review"
    private const val C_USER_BADGE = "user_badges"

    private const val C_NOTIFICATIONS = "notifications"
    private const val C_ITINERARIES = "itineraries"

    private const val TRAVEL_IMAGES_BUCKET = "travel-images"
    private const val USER_IMAGES_BUCKET = "user-images"


    // Format: https://project.supabase.co/storage/v1/object/public/travel-images/userId/tripId/filename
    const val TRAVEL_IMAGES_BUCKET_PREFIX = "/storage/v1/object/public/travel-images/"
    const val USER_IMAGES_BUCKET_PREFIX = "/storage/v1/object/public/user-images/"

    private val db: FirebaseFirestore
        get() = Firebase.firestore

    private val supabaseClient: SupabaseClient

    val auth = FirebaseAuth.getInstance()

    init {
        db.firestoreSettings = FirebaseFirestoreSettings.Builder()
            //https://firebase.google.com/docs/firestore/manage-data/enable-offline#kotlin
            // Guide is a little outdated, but the settings are still relevant
            // Use memory cache
            .setLocalCacheSettings(memoryCacheSettings {})
            // Use persistent disk cache (default)
            // the value is a long that represents the maximum size of the cache in bytes
            .setLocalCacheSettings(persistentCacheSettings { FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED })
            .build()

        supabaseClient = createSupabaseClient(
            supabaseUrl = Secrets.SUPABASE_PROJECT_URL,
            supabaseKey = Secrets.SUPABASE_ANON_KEY
        ) {
            install(Postgrest)
            install(Storage)
        }
    }

    val storage: Storage = supabaseClient.storage

    val travelProposals = db.collection(C_TRAVEL_PROPOSALS)
    val travelImagesBucket = storage.from(TRAVEL_IMAGES_BUCKET)
    val userImagesBucket = storage.from(USER_IMAGES_BUCKET)

    val notifications = db.collection(C_NOTIFICATIONS)
    val itineraries = db.collection(C_ITINERARIES)

    val users = db.collection(C_USERS)
    val userReview = db.collection(C_USER_REVIEW)

    fun getReviewCollection(tripId: String): CollectionReference {
        Log.d("Collections", "Getting review collection for tripId: $tripId")
        return travelProposals.document(tripId).collection(C_TRAVEL_REVIEW)
    }

    fun getBadgeCollection(userUID: String): CollectionReference {
        Log.d("Collections", "Getting badge collection for userUID: $userUID")
        return users.document(userUID).collection(C_USER_BADGE)
    }
}

// data class Test(
//     val first: String = "",
//     val second: String = "",
//     val timestamp: Timestamp = Timestamp.now()
// )

// class TestModel() {
//     fun getTests(): Flow<List<Test>> = callbackFlow {
//         val listener = Collections.tests.addSnapshotListener { s, error ->
//             if (s != null) {
//                 trySend(s.toObjects(Test::class.java))
//             } else {
//                 Log.e("TestModel", error.toString())
//                 trySend(emptyList())
//             }
//         }

//         awaitClose {
//             listener.remove()
//         }
//     }
// }

// class TestViewModel(private val model: TestModel) : ViewModel() {
//     val tests: Flow<List<Test>> = model.getTests()
// }


// object TestFactory : ViewModelProvider.Factory {
//     private val model = TestModel()

//     override fun <T : ViewModel> create(modelClass: Class<T>): T {
//         return when {
//             modelClass.isAssignableFrom(TestViewModel::class.java) ->
//                 TestViewModel(model) as T

//             else -> throw IllegalArgumentException("Unknown ViewModel")
//         }
//     }
// }


// @Composable
// fun TestScreen(vm: TestViewModel = viewModel(factory = TestFactory)) {
//     val tests by vm.tests.collectAsState(initial = listOf())

//     LazyColumn(modifier = Modifier.fillMaxSize()) {
//         items(tests){
//            TestCard(it)
//             Spacer(Modifier.height(12.dp))
//         }
//     }
// }

// @Composable
// fun TestCard(test: Test) {
//     Row{
//         Text(
//             text = test.first,
//             modifier = Modifier
//                 .fillMaxWidth()
//                 .padding(16.dp)
//                 .background(MaterialTheme.colorScheme.primary),
//             style = MaterialTheme.typography.bodyLarge,
//             color = MaterialTheme.colorScheme.onPrimary
//         )
//         Spacer(modifier = Modifier.height(8.dp))
//         Text(
//             text = test.second,
//             modifier = Modifier
//                 .fillMaxWidth()
//                 .padding(16.dp)
//                 .background(MaterialTheme.colorScheme.secondary),
//             style = MaterialTheme.typography.bodyMedium,
//             color = MaterialTheme.colorScheme.onSecondary
//         )
//         Spacer(modifier = Modifier.height(8.dp))
//         Text(
//             text = test.timestamp.toDate().toString(),
//             modifier = Modifier
//                 .fillMaxWidth()
//                 .padding(16.dp)
//                 .background(MaterialTheme.colorScheme.tertiary),
//             style = MaterialTheme.typography.bodySmall,
//             color = MaterialTheme.colorScheme.onTertiary
//         )
//     }
// }

