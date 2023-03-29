package com.github.factotum_sdp.factotum.ui.display

/*
@RunWith(AndroidJUnit4::class)
class DisplayFragmentTest {

    private lateinit var scenario: FragmentScenario<DisplayFragment>
    private lateinit var context: Context

    @Before
    fun setUp() {
        scenario = launchFragmentInContainer(themeResId = R.style.Theme_Factotum)
        Firebase.storage.useEmulator("10.0.2.2", 9199)
        context = InstrumentationRegistry.getInstrumentation().context
    }

    @After
    fun tearDown() {
       emptyStorageEmulator(Firebase.storage.reference)
    }

    @Test
    fun displayFragment_uiElementsDisplayed() {
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()))
        onView(withId(R.id.refreshButton)).check(matches(isDisplayed()))
    }

    @Test
    fun displayFragment_recyclerViewHasCorrectLayoutManager() {
        scenario.onFragment { fragment ->
            val recyclerView = fragment.requireView().findViewById<RecyclerView>(R.id.recyclerView)
            assert(recyclerView.layoutManager is LinearLayoutManager)
        }
    }

    @Test
    fun displayFragment_refreshButtonClicked() {
        onView(withId(R.id.refreshButton)).perform(click())
    }

    @Test
    fun displayFragment_displayOnlyOnePhotoIfSame() {
        runBlocking {
            val imagePath = "test_image1.jpg"
            uploadImageToStorageEmulator(context, imagePath, "test_image1.jpg")
        }

        Thread.sleep(WAIT_TIME_INIT)

        onView(withId(R.id.refreshButton)).perform(click())

        Thread.sleep(WAIT_TIME_REFRESH)

        runBlocking {
            val imagePath = "test_image1.jpg"
            uploadImageToStorageEmulator(context, imagePath, "test_image1.jpg")
        }

        Thread.sleep(WAIT_TIME_INIT)

        onView(withId(R.id.refreshButton)).perform(click())

        Thread.sleep(WAIT_TIME_REFRESH)

        scenario.onFragment { fragment ->
            val recyclerView = fragment.requireView().findViewById<RecyclerView>(R.id.recyclerView)
            assert(recyclerView.adapter?.itemCount == 1)
        }
    }
}

 */
