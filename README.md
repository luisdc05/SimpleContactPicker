# SimpleContactPicker

SimpleContactPicker is a library that shows a view that loads and allows the user to select contacts from the android device.

### Features

- Select/deselect contacts by clicking on them
- Search through the contacts by their name or number
- Preselect contacts
- Hide contacts

<img src="images/pic1.png" height="500px">
<img src="images/pic2.png" height="500px">
<img src="images/pic3.png" height="500px">

### Download
1. Add the JitPack repository to your build file
 ```javascript
    allprojects {
    	repositories {
    		...
    		maven { url "https://jitpack.io" }
    	}
    }
```

2. Add the SimpleContactPicker dependency
```javascript
dependencies {
  compile 'com.github.luisdc05:SimpleContactPicker:v0.5.0'
}
```

#### Todos

- Allow for custom contacts to be injected (this can be useful if contacts come from a backend)
- Allow to use custom views for selected and listed contacts
- Allow to use a custom projection (right now it only loads mobile type contacts)
- Load the contacts on the background
