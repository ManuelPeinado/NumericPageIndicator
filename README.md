NumericPageIndicator
====================

A ViewPager page indicator that displays the current page number and (optionally) the page count. It can also display buttons to go to adjacent pages, and to move to the start and end pages.

Integrates seamlessly with Jake Wharton's [ViewPagerIndicator][1] library.

![Example Image][2]

Try out the sample application:

<a href="https://play.google.com/store/apps/details?id=com.manuelpeinado.numericpageindicator.demo">
  <img alt="Android app on Google Play"
       src="https://developer.android.com/images/brand/en_app_rgb_wo_45.png" />
</a>

Or browse the [source code of the sample application][3] for a complete example of use.


Including in your project
-------------------------

If you’re using the Eclipse ADT plugin you can include NumericPageIndicator as a library project. Create a new Android project using the library/ folder as the existing source. Then, open the properties of this new project and, in the 'Android' category, add a reference to the ViewPagerIndicator library project. Finally, in your application project properties, add a reference to the created library project.

If you use maven to build your Android project you can simply add a dependency for this library.

```xml
<dependency>
    <groupId>com.github.manuelpeinado.numericpageindicator</groupId>
    <artifactId>library</artifactId>
    <version>1.1.0</version>
    <type>apklib</type>
</dependency>
```

Usage
-----

Add a <tt><com.manuelpeinado.numericpageindicator.NumericPageIndicator></tt> element to your XML layout and attach it to your ViewPager in your Java code just like you would with any of the built-in ViewPagerIndicator classes. 

See the accompanying sample application for a complete example.

Customization
-------------

You can customize the look of the indicators in any of the following ways:

 1. **Theme XML**. Define a <tt>numericPageIndicatorStyle</tt> attribute in your theme and make it reference a custom style where you can customize any of the multiple attributes supported by the library.
 2. **Layout XML**. Include any of the attributes supported by the library directly in your <tt>NumericPageIndicator</tt> element.
 3. **Object methods**. Use the getters and setters methods provided by the library from your Java code.

The sample application includes one activity to illustrate each of these methods.

Who's using it
--------------
 
*Does your app use NumericPageIndicator? If you want to be featured on this list drop me a line.*

Developed By
--------------------

Manuel Peinado Gallego - <manuel.peinado@gmail.com>

<a href="https://twitter.com/mpg2">
  <img alt="Follow me on Twitter"
       src="https://raw.github.com/ManuelPeinado/NumericPageIndicator/master/art/twitter.png" />
</a>
<a href="https://plus.google.com/106514622630861903655">
  <img alt="Follow me on Twitter"
       src="https://raw.github.com/ManuelPeinado/NumericPageIndicator/master/art/google-plus.png" />
</a>
<a href="http://www.linkedin.com/pub/manuel-peinado-gallego/1b/435/685">
  <img alt="Follow me on Twitter"
       src="https://raw.github.com/ManuelPeinado/NumericPageIndicator/master/art/linkedin.png" />
</a>

License
-------

    Copyright 2013 Manuel Peinado

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
    
[1]: http://viewpagerindicator.com
[2]: https://raw.github.com/ManuelPeinado/NumericPageIndicator/master/art/readme_pic.png
[3]: https://github.com/ManuelPeinado/NumericPageIndicator/tree/master/sample
