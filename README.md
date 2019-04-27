# Picture Frame

This project provides a custom component that symplifies the display of images
with mouse interaction like in any image viewer.

By default the image is fitted to show the full image in the picture frame even
when is resized, this is achieved constantly updating the zoom level with the 
minimum zoom value whenever necessary. Change the zoom level, which can be donde
with the mouse wheel by default, disables the fitted status and allows move the
image by draggin it with the mouse.

The mouse interaction can be disabled with the `setDynamic(boolean)` method.

Since `PictureFrame` inherits from `JComponent`, it can be used like any other
Java Swing component.

# Examples of use

How to set an image into a `PictureFrame` and then add it into a `JFrame`.

```java
JFrame frame = new JFrame();
PictureFrame viewer = new PictureFrame();
BufferedImage image = ImageIO.read("pic.jpg");
viewer.setImage(image);
frame.add(viewer);
```

The `PictureFrame` can be instantiated at once with the image to be displayed.
The image can also be changed at any time, even after the `PictureFrame` is
added to another component.

```java
JFrame frame = new JFrame();
PictureFrame viewer = new PictureFrame(ImageIO.read("pic.jpg"));
frame.add(viewer);
frame.setVisible(true);

viewer.setImage(ImageIO.read("other_pic.jpg"));
```

# Reference

The full and detailed reference can be builded with `javadoc`.

## Constructors

### `PictureFrame()`

Creates a new `PictureFrame` without any image and all default values.

### `PictureFrame(BufferedImage img)`

Creates a new `PictureFrame` with the given image and all default values. The
image is fitted by default.

## Public methods

### `void setDynamic(boolean status)`

Sets the `dynamic` status.

When the status is false the mouse events are ignored.

### `void setAntialiasing(boolean status)`

Sets the `antialiasing` status.

When the status is true and zoom is less than 1, the image is drawed with
antialiasing.

### `void setZoom(float zoom_new, Point point)`

Sets the `zoom` level.

The `zoom_new` is clamped between `zoom_min` and `zoom_max` before to assign to
`zoom` and updates the `location` and `size` attributes.

If the final value of `zoom` is equal to `zoom_min`, the `fitted` flag is setted
`true`.

If `point` is `null`, the center of the component is used as the mouse position.

### `void setMaxZoom(float zoom_max_new)`

Sets the maximum zoom.

By default the maximum zoom level is `20` and cannot be less `1`. If the given
value is less than `1`, it will lead to `1`.

### `void setImageLocation(Point point)`

Sets the image location.

The `point` component values are clamped to ensure that will be valid.

### `void setImageLocation(int x, int y)`

Sets the image location.

The `x` and `y` values are clamped to ensure that will be valid.

### `void setImage(BufferedImage image_new, boolean fit_image)`

Sets a new image.

If `fit_image` is `true`, the image will be fitted into the picture frame.

### `float getZoom()`

Gets the `zoom` level.

### `float getMinZoom()`

Gets the `zoom_min` level.

### `float getMaxZoom()`

Gets the `zoom_max` level.

### `Point getImageLocation()`

Gets the image `location` in the picture frame.

### `Dimension getImageSize()`

Gets the image `size` for the current `zoom` level.

### `BufferedImage getImage()`

Gets the image.

### `boolean isDynamic()`

Gets the `dynamic` status.

### `boolean isAntialiasing()`

Gets the `antialiasing` status.

### `boolean isFitted()`

Gets the `fitted` status.

### `boolean isOriginal()`

Checks if the `image` is in the original size, that is, the `zoom` level is `1`.

### `void paintComponent(Graphics g)`

Overried from `JComponent`. The `image` is drawed at the stored `location` and with `size`.

Antialiasing is applied if the `antialiasing` flag is `true` and the `zoom` level is less than `1`.

### `void fit()`

Adjust the image to the windows size.

Sets `zoom_min` as the `zoom` level using the center of the picture frame as the
location to apply the `zoom`.

### `void original()`

Show the image at the original size.

Sets `1` as the `zoom` level using the center of the picture frame as the
location to apply the `zoom`.

### `void zoomIn(Point point)`

Zoom in the `image` over the mouse position.

If the mouse position is `null`, then the center of the picture frame is used
as the location to apply the `zoom`.

### `void zoomOut(Point point)`

Zoom out the `image` over the mouse position.

If the mouse position is `null`, then the center of the picture frame is used
as the location to apply the `zoom`.

### `void moveImage(Point distance)`

Move the `image` on the picture frame.

The given `distance` is added to the current `location` and clamped to correct
values.

# Table of contents
- [Picture Frame](#picture-frame)
- [Examples of use](#examples-of-use)
- [Reference](#reference)
  - [Constructors](#constructors)
    - [`PictureFrame()`](#pictureframe)
    - [`PictureFrame(BufferedImage img)`](#pictureframebufferedimage-img)
  - [Public methods](#public-methods)
    - [`void setDynamic(boolean status)`](#void-setdynamicboolean-status)
    - [`void setAntialiasing(boolean status)`](#void-setantialiasingboolean-status)
    - [`void setZoom(float zoom_new, Point point)`](#void-setzoomfloat-zoomnew-point-point)
    - [`void setMaxZoom(float zoom_max_new)`](#void-setmaxzoomfloat-zoommaxnew)
    - [`void setImageLocation(Point point)`](#void-setimagelocationpoint-point)
    - [`void setImageLocation(int x, int y)`](#void-setimagelocationint-x-int-y)
    - [`void setImage(BufferedImage image_new, boolean fit_image)`](#void-setimagebufferedimage-imagenew-boolean-fitimage)
    - [`float getZoom()`](#float-getzoom)
    - [`float getMinZoom()`](#float-getminzoom)
    - [`float getMaxZoom()`](#float-getmaxzoom)
    - [`Point getImageLocation()`](#point-getimagelocation)
    - [`Dimension getImageSize()`](#dimension-getimagesize)
    - [`BufferedImage getImage()`](#bufferedimage-getimage)
    - [`boolean isDynamic()`](#boolean-isdynamic)
    - [`boolean isAntialiasing()`](#boolean-isantialiasing)
    - [`boolean isFitted()`](#boolean-isfitted)
    - [`boolean isOriginal()`](#boolean-isoriginal)
    - [`void paintComponent(Graphics g)`](#void-paintcomponentgraphics-g)
    - [`void fit()`](#void-fit)
    - [`void original()`](#void-original)
    - [`void zoomIn(Point point)`](#void-zoominpoint-point)
    - [`void zoomOut(Point point)`](#void-zoomoutpoint-point)
    - [`void moveImage(Point distance)`](#void-moveimagepoint-distance)
- [Table of contents](#table-of-contents)