import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'dart:async';
import 'dart:io';

const MethodChannel flutterChannel = MethodChannel('FLUTTER_CHANNEL');
const MethodChannel androidChannel = MethodChannel('ANDROID_CHANNEL');

dynamic image ;
HomeState homestate ;

void main() => runApp(new MyApp());

Future<dynamic> flutterHandler(MethodCall call){
  if(call.method.endsWith('nextImage')){
    image = File(call.arguments);
    homestate.setImage();
  }
  return null ;
}


class MyApp extends StatelessWidget {

  MyApp(){
    flutterChannel.setMethodCallHandler(flutterHandler);
  }

  @override
  Widget build(BuildContext context) {
    // TODO: implement build
    return new MaterialApp(
      theme: new ThemeData(
        primarySwatch: Colors.lightBlue,
      ),
      home: new HomePage(),
    );
  }
}

class HomePage extends StatefulWidget{
  @override
  State<StatefulWidget> createState() {
    // TODO: implement createState
    homestate = new HomeState();
    return homestate ;
  }
}

class HomeState extends State<HomePage>{

  int start_flag = 0 ;

  setImage(){
    setState(() {});
  }

  loadImage() async{
    new Timer(new Duration(seconds: 5),timerHandle );
  }

  Future<dynamic> timerHandle () async{
    androidChannel.invokeMethod('loadImage');
  }

  @override
  Widget build(BuildContext context) {

    if(start_flag == 0) {
      loadImage();
      start_flag = 1;
    }

    return new Scaffold(

      appBar: new AppBar(
        title: new Text('Image Viewer'),
        centerTitle: true,
      ),

      body: image == null ?
        new Center(
          child: new Text('....LOADING IMAGES....'+"\n\n"+
              'Hint : Wave your hands over proximity '
              'sensor (near front camera) to change images !!',
          style: new TextStyle(color: Colors.purple,fontSize: 18.0),
            textAlign: TextAlign.center,
          ),
        ):
      new Center(
        child: new Image.file(image),
      ),
    );
  }
}