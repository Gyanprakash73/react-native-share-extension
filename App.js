import React, {useEffect, useState} from 'react';
import {View, Image, Text, Button, Platform, NativeModules} from 'react-native';
import RNFS from 'react-native-fs';

const {SharedImageModule} = NativeModules;

const App = () => {
  const [sharedImage, setSharedImage] = useState(null);

  useEffect(() => {
    if (Platform.OS == 'ios') {
      fetchSharedImage();
    } else {
      fetchSharedImageAndroid();
    }
  }, []);

  const fetchSharedImage = async () => {
    try {
      // Get the app group directory path
      const appGroupPath = await RNFS.pathForGroup(
        'group.com.gyan.AwesomeProject',
      );
      const imagePath = `${appGroupPath}/sharedImage.png`;

      // Check if the image file exists
      const fileExists = await RNFS.exists(imagePath);
      if (fileExists) {
        // Use the file URI to display the image
        setSharedImage(`file://${imagePath}`);
      } else {
        console.log('No shared image found');
      }
    } catch (error) {
      console.error('Error fetching shared image:', error);
    }
  };

  const fetchSharedImageAndroid = () => {
    SharedImageModule.getSharedImage()
      .then(result => {
        if (result?.type === 'image' && result?.value) {
          console.log('Shared image:', result.value);
          setSharedImage(result.value);
        }
      })
      .catch(console.error);
  };

  const removeSharedImage = async () => {
    try {
      // Get the app group directory path
      const appGroupPath = await RNFS.pathForGroup(
        'group.com.gyan.AwesomeProject',
      );
      const imagePath = `${appGroupPath}/sharedImage.png`;

      // Check if the image file exists
      const fileExists = await RNFS.exists(imagePath);
      if (fileExists) {
        // Delete the image file
        await RNFS.unlink(imagePath);
        console.log('Image removed successfully');
        setSharedImage(null); // Clear the image from the state
      } else {
        console.log('No shared image found to remove');
      }
    } catch (error) {
      console.error('Error removing shared image:', error);
    }
  };

  const removeSharedImageAndroid = () => {
    SharedImageModule.clearSharedImage()
      .then(() => {
        setSharedImage(null);
        console.log('Shared image cleared');
      })
      .catch(console.error);
  };

  return (
    <View style={{flex: 1, alignItems: 'center', paddingTop: 50}}>
      {sharedImage ? (
        <>
          <Image
            source={{uri: sharedImage}}
            style={{width: 400, height: 400, resizeMode: 'contain'}}
          />
          <Button
            title="Remove Image"
            onPress={() =>
              Platform.OS == 'ios'
                ? removeSharedImage()
                : removeSharedImageAndroid()
            }
          />
        </>
      ) : (
        <>
          <Text>No image shared</Text>
          <Button
            title="Get Image"
            onPress={() =>
              Platform.OS == 'ios'
                ? fetchSharedImage()
                : fetchSharedImageAndroid()
            }
          />
        </>
      )}
    </View>
  );
};

export default App;
