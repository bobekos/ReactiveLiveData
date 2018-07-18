# ReactiveLiveData

An RxJava Extension for the LiveData observer introduced by Google. Provides the ability to perform single actions using RxJava and takes advantage of an automatic subscription of the Lifecycle owner. Mainly designed to used Room CRUD commands with RxJava.

## Getting Started

### Setting up the dependency

...

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
                    onComplete = {
                        showToast("User inserted")
                    },
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
                    onError = {
                        showToast(it.message)
                    },
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
                nullObserver = {
                    showToast("Value is null")
                })
```

## Features

...

## Testing

...

## Resources and Credits

* [google android viewmodel](https://developer.android.com/topic/libraries/architecture/viewmodel)
* [google android room](https://developer.android.com/topic/libraries/architecture/room)
* [rxJava](https://github.com/ReactiveX/RxJava)
* [rxAndroid](https://github.com/ReactiveX/RxAndroid)
* [inspiration](https://medium.com/google-developers/room-rxjava-acb0cd4f3757)

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE.md](LICENSE.md) file for details
