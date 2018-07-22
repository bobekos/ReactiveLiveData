# ReactiveLiveData [![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-ReactiveLiveData-green.svg?style=flat )]( https://android-arsenal.com/details/1/7052 ) [![](https://img.shields.io/badge/minSdk-16-brightgreen.svg)](https://github.com/bobekos/ReactiveLiveData)

An RxJava Extension for the LiveData observer introduced by Google. Provides the ability to perform single actions using RxJava and takes advantage of an automatic subscription of the Lifecycle owner. Mainly designed to used Room CRUD commands with RxJava.

## Why this lib?

[Medium article](https://medium.com/@bobek.bobekos/android-room-livedata-and-rxjava-c6aa0aac9b2c)

## Getting Started

### Setting up the dependency

[![](https://api.bintray.com/packages/bobekos/maven/ReactiveLiveData/images/download.svg)](https://bintray.com/bobekos/maven/ReactiveLiveData/_latestVersio)
```
implementation 'com.github.bobekos:reactivelivedata:x.x.x'
```

## Usage

Just use one of the available reactiveSource classes:

#### CompletableReactiveSource

```kotlin
//ViewModel
class UserViewModel(private val dao: UserDao) : ViewModel() {

    fun insert(id: Int, name: String): LiveData<Optional<Nothing>> {
        return CompletableReactiveSource.fromAction {
            dao.insert(UserEntity(id, name))
        }
    }
}

//Activity/Fragment/etc.
...
//short
viewModel.insert(1, "User").subscribeCompletable(this)
//or with callback
viewModel.insert(1, "Bobekos").subscribeCompletable(this,
                    //optional
                    onComplete = {
                        showToast("User inserted")
                    },
                    //optional
                    onError = {
                        showToast(it.message)
                    })
```

#### SingleReactiveSource

```kotlin
//ViewModel
class UserViewModel(private val dao: UserDao) : ViewModel() {

    fun getFromSingle(id: Int): LiveData<Optional<UserEntity>> {
        return SingleReactiveSource.from(dao.getByIdAsSingle(id))
    }
}

//Activity/Fragment/etc.
...
viewModel.getFromSingle(1).subscribeSingle(this,
                    onSuccess = {
                        showToast("User ${it.username} loaded")
                    },
                    //optional
                    onError = {
                        showToast(it.message)
                    })
```

#### MaybeReactiveSource

```kotlin
//ViewModel
class UserViewModel(private val dao: UserDao) : ViewModel() {

    fun getFromMaybe(id: Int): LiveData<Optional<UserEntity>> {
        return MaybeReactiveSource.from(dao.getByIdAsMaybe(id))
    }
}

//Activity/Fragment/etc.
...
viewModel.getFromMaybe(1).subscribeMaybe(this,
                    onSuccess = {
                        showToast("User ${it.username} loaded")
                    },
                    //optional
                    onError = {
                        showToast(it.message)
                    },
                    //optional
                    onComplete = {
                        showToast("No user found")
                    })
```

#### NullSafe extension for LiveDataReactiveStreams

```kotlin
//ViewModel
class UserViewModel(private val dao: UserDao) : ViewModel() {

    fun loadUser(): LiveData<UserEntity> {
        return LiveDataReactiveStreams.fromPublisher(dao.getUsers())
    }
}

//Activity/Fragment/etc.
...
//short
viewModel.loadUser().nonNullObserver(this, observer = {
            showToast("I'm not null ${it.username}")
        })
//or with null callback
viewModel.loadUser().nonNullObserver(this,
                observer = {
                    showToast("I'm observing ${it.username}")
                },
                //optional
                nullObserver = {
                    showToast("Value is null")
                })
```

## Testing

For each reactive source there is a specific test method.
```kotlin
liveData.testCompletableSubscribe(...)
liveData.testMaybeSubscribe(...)
liveData.testSingleSubscribe(...)
```

Make sure to include the 'InstantTastExecutorRule' (core-testing) into your tests. Furthermore, the default IoSchedulerHandler (or the scheduler which you used) should be overwritten.

```kotlin
@RunWith(JUnit4::class)
class UserViewModelTest {
    
    private inline fun <reified T> lambdaMock(): T = Mockito.mock(T::class.java)
    
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()
    
    private val userDao = mock(UserDao::class.java)
    private val viewModel = UserViewModel(userDao)
    
    @Before
    fun setup() {
        RxJavaPlugins.setIoSchedulerHandler {
            Schedulers.trampoline()
        }
    }
    
    @Test
    fun testGetFromSingleSuccess() {
        val testObject = UserEntity(1, "Bobekos")

        `when`(userDao.getByIdAsSingle(1)).then { Single.just(testObject) }

        val lifecycle = LifecycleRegistry(mock(LifecycleOwner::class.java))
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        val observer = lambdaMock<(t: UserEntity) -> Unit>()

        viewModel.getFromSingle(1).testSingleSubscribe(lifecycle, onSuccess = observer)

        verify(observer).invoke(testObject)
    }
    
    @Test
    fun testGetFromSingleError() {
        val testObject = SQLiteConstraintException()

        `when`(userDao.getByIdAsSingle(1)).then { Single.error<UserEntity>(testObject) }

        val lifecycle = LifecycleRegistry(mock(LifecycleOwner::class.java))
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        val observer = lambdaMock<(e: Throwable) -> Unit>()

        viewModel.getFromSingle(1).testSingleSubscribe(lifecycle, onError = observer)

        verify(observer).invoke(testObject)
    }
```

For more tests look into the sample [app](https://github.com/bobekos/ReactiveLiveData/blob/master/app/src/test/java/com/github/bobekos/rxviewmodelexample/viewmodel/UserViewModelTest.kt)

## Resources and Credits

* [google android viewmodel](https://developer.android.com/topic/libraries/architecture/viewmodel)
* [google android room](https://developer.android.com/topic/libraries/architecture/room)
* [rxJava](https://github.com/ReactiveX/RxJava)
* [rxAndroid](https://github.com/ReactiveX/RxAndroid)
* [inspiration](https://medium.com/google-developers/room-rxjava-acb0cd4f3757)

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE.md](LICENSE.md) file for details
