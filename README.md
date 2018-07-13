# RxViewModel

An RxJava Extension to the ViewModel introduced by Google. Provides the ability to perform single actions using RxJava and takes advantage of an automatic subscription of the ViewModel. Mainly designed to used Room CRUD commands with RxJava.

## Getting Started

### Setting up the dependency

...

## Usage

Just extend your existing ViewModel with the RxViewModel class:

```kotlin
class UserViewModel(private val dao: UserDao) : RxViewModel() {

    fun insert(id: Int, name: String): CompletableAction {
        return CompletableAction { dao.insert(UserEntity(id, name)) }
    }

    fun getFromMaybe(id: Int): ActionFromMaybe<UserEntity> {
        return ActionFromMaybe(dao.getByIdAsMaybe(id))
    }

    fun loadUsers() : LiveData<UserEntity> {
        return liveDataFromFlowable(dao.getUsers())
    }

    fun delete(id: Int, name: String): CompletableAction {
        return CompletableAction { dao.delete(UserEntity(id, name)) }
    }

}
```

## Features

### CompletableAction

...

### ActionFromSingle

...

### ActionFromMaybe

...

### LiveDataFromFlowable

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
