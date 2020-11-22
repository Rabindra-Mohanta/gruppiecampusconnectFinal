/*
   Copyright 2012 Harri Smatt

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package school.campusconnect.curl;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.RectF;
import android.opengl.GLSurfaceView;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import school.campusconnect.R;


/**
 * OpenGL ES View.
 * 
 * @author harism
 */
public class CurlView extends GLSurfaceView implements /* View.OnTouchListener, */
CurlRenderer.Observer {

	// Curl state. We are flipping none, left or right page.
	private static final int CURL_LEFT = 1;
	private static final int CURL_NONE = 0;
	private static final int CURL_RIGHT = 2;

	// Constants for mAnimationTargetEvent.
	private static final int SET_CURL_TO_LEFT = 1;
	private static final int SET_CURL_TO_RIGHT = 2;

	// Shows one page at the center of view.
	public static final int SHOW_ONE_PAGE = 1;
	// Shows two pages side by side.
	public static final int SHOW_TWO_PAGES = 2;

	private boolean mAllowLastPageCurl = true;

	private boolean mAnimate = false;
	private long mAnimationDurationTime = 300;
	private PointF mAnimationSource = new PointF();
	private long mAnimationStartTime;
	private PointF mAnimationTarget = new PointF();
	private int mAnimationTargetEvent;

	private PointF mCurlDir = new PointF();

	private PointF mCurlPos = new PointF();
	private int mCurlState = CURL_NONE;
	// Current bitmap index. This is always showed as front of right page.
	private int mCurrentIndex = 0;

	// Start position for dragging.
	private PointF mDragStartPos = new PointF();

	private boolean mEnableTouchPressure = false;
	// Bitmap size. These are updated from renderer once it's initialized.
	private int mPageBitmapHeight = -1;

	private int mPageBitmapWidth = -1;
	// Page meshes. Left and right meshes are 'static' while curl is used to
	// show page flipping.
	private CurlMesh mPageCurl;

	private CurlMesh mPageLeft;
	private PageProvider mPageProvider;
	private CurlMesh mPageRight;

	private PointerPosition mPointerPos = new PointerPosition();

	private CurlRenderer mRenderer;
	private boolean mRenderLeftPage = true;
	private SizeChangedObserver mSizeChangedObserver;

	// One page is the default.
	private int mViewMode = SHOW_ONE_PAGE;

	public static CurlView cv;

	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;
	private int mode = NONE;

	private PointF start = new PointF();
	private PointF mid = new PointF();
	private float oldDist = 1f;
	public PanZoomManager panZoomManager;
	private static final int CENTER = 0;
	private float maxZoom;
	

	private OnPageChangeListener pageChangeListener;

	/**
	 * Default constructor.
	 */
	public CurlView(Context ctx) {
		super(ctx);
		init(ctx);
	}

	/**
	 * Default constructor.
	 */
	public CurlView(Context ctx, AttributeSet attrs) {
		super(ctx, attrs);
		init(ctx);
	}

	/**
	 * Default constructor.
	 */
	public CurlView(Context ctx, AttributeSet attrs, int defStyle) {
		this(ctx, attrs);
	}

	/**
	 * Get current page index. Page indices are zero based values presenting
	 * page being shown on right side of the book.
	 */
	public int getCurrentIndex() {
		return mCurrentIndex;
	}

	/**
	 * Initialize method.
	 */
	private void init(Context ctx) {

		cv = this;
		mRenderer = new CurlRenderer(this);
		
		setRenderer(mRenderer);
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

		// Even though left and right pages are static we have to allocate room
		// for curl on them too as we are switching meshes. Another way would be
		// to swap texture ids only.
		mPageLeft = new CurlMesh(10);
		mPageRight = new CurlMesh(10);
		mPageCurl = new CurlMesh(10);
		mPageLeft.setFlipTexture(true);
		mPageRight.setFlipTexture(false);
		
		TypedValue outValue = new TypedValue();
		getResources().getValue(R.dimen.max_zoom_value, outValue, true);
		maxZoom = outValue.getFloat();  

		panZoomManager = new PanZoomManager(this, CENTER);
	}

	@Override
	public void onDrawFrame() {
		// We are not animating.
		if (mAnimate == false) {
			return;
		}

		long currentTime = System.currentTimeMillis();
		// If animation is done.
		if (currentTime >= mAnimationStartTime + mAnimationDurationTime) {
			if (mAnimationTargetEvent == SET_CURL_TO_RIGHT) {

				// Switch curled page to right.
				CurlMesh right = mPageCurl;
				CurlMesh curl = mPageRight;
				right.setRect(mRenderer.getPageRect(CurlRenderer.PAGE_RIGHT));
				right.setFlipTexture(false);
				right.reset();
				mRenderer.removeCurlMesh(curl);
				mPageCurl = curl;
				mPageRight = right;
				// If we were curling left page update current index.
				if (mCurlState == CURL_LEFT) {
					--mCurrentIndex;
				}
			} else if (mAnimationTargetEvent == SET_CURL_TO_LEFT) {

				// Switch curled page to left.
				CurlMesh left = mPageCurl;
				CurlMesh curl = mPageLeft;
				left.setRect(mRenderer.getPageRect(CurlRenderer.PAGE_LEFT));
				left.setFlipTexture(true);
				left.reset();
				mRenderer.removeCurlMesh(curl);
				if (!mRenderLeftPage) {
					mRenderer.removeCurlMesh(left);
				}
				mPageCurl = curl;
				mPageLeft = left;
				// If we were curling right page update current index.
				if (mCurlState == CURL_RIGHT) {
					++mCurrentIndex;
				}
			}
			mCurlState = CURL_NONE;
			mAnimate = false;
			requestRender();
		} else {
			mPointerPos.mPos.set(mAnimationSource);
			float t = 1f - ((float) (currentTime - mAnimationStartTime) / mAnimationDurationTime);
			t = 1f - (t * t * t * (3 - 2 * t));
			mPointerPos.mPos.x += (mAnimationTarget.x - mAnimationSource.x) * t;
			mPointerPos.mPos.y += (mAnimationTarget.y - mAnimationSource.y) * t;
			updateCurlPos(mPointerPos);
		}

		pageChangeListener.pageChanged(mCurrentIndex);
	}

	@Override
	public void onPageSizeChanged(int width, int height) {

		mPageBitmapWidth = width;
		mPageBitmapHeight = height;
		
		
		updatePages();
		requestRender();
	}

	@Override
	public void onSizeChanged(int w, int h, int ow, int oh) {
		
		super.onSizeChanged(w, h, ow, oh);
		requestRender();
		if (mSizeChangedObserver != null) {
			
			mSizeChangedObserver.onSizeChanged(w, h);
		}
	}

	@Override
	public void onSurfaceCreated() {
		// In case surface is recreated, let page meshes drop allocated texture
		// ids and ask for new ones. There's no need to set textures here as
		// onPageSizeChanged should be called later on.
		mPageLeft.resetTexture();
		mPageRight.resetTexture();
		mPageCurl.resetTexture();
	}

	private boolean touchDown() {

		RectF rightRect = mRenderer.getPageRect(CurlRenderer.PAGE_RIGHT);
		RectF leftRect = mRenderer.getPageRect(CurlRenderer.PAGE_LEFT);

		// Once we receive pointer down event its position is mapped to
		// right or left edge of page and that'll be the position from where
		// user is holding the paper to make curl happen.
		mDragStartPos.set(mPointerPos.mPos);

		// First we make sure it's not over or below page. Pages are
		// supposed to be same height so it really doesn't matter do we use
		// left or right one.
		if (mDragStartPos.y > rightRect.top) {
			mDragStartPos.y = rightRect.top;
		} else if (mDragStartPos.y < rightRect.bottom) {
			mDragStartPos.y = rightRect.bottom;
		}

		// Then we have to make decisions for the user whether curl is going
		// to happen from left or right, and on which page.
		if (mViewMode == SHOW_TWO_PAGES) {
			// If we have an open book and pointer is on the left from right
			// page we'll mark drag position to left edge of left page.
			// Additionally checking mCurrentIndex is higher than zero tells
			// us there is a visible page at all.
			if (mDragStartPos.x < rightRect.left && mCurrentIndex > 0) {
				mDragStartPos.x = leftRect.left;
				startCurl(CURL_LEFT);
			}
			// Otherwise check pointer is on right page's side.
			else if (mDragStartPos.x >= rightRect.left && mCurrentIndex < mPageProvider.getPageCount()) {
				mDragStartPos.x = rightRect.right;
				if (!mAllowLastPageCurl && mCurrentIndex >= mPageProvider.getPageCount() - 1) {
					return false;
				}
				startCurl(CURL_RIGHT);
			}
		} else if (mViewMode == SHOW_ONE_PAGE) {
			float halfX = (rightRect.right + rightRect.left) / 2;
			if (mDragStartPos.x < halfX && mCurrentIndex > 0) {
				mDragStartPos.x = rightRect.left;
				startCurl(CURL_LEFT);
			} else if (mDragStartPos.x >= halfX && mCurrentIndex < mPageProvider.getPageCount()) {
				mDragStartPos.x = rightRect.right;
				if (!mAllowLastPageCurl && mCurrentIndex >= mPageProvider.getPageCount() - 1) {
					return false;
				}
				startCurl(CURL_RIGHT);
			}
		}
		// If we have are in curl state, let this case clause flow through
		// to next one. We have pointer position and drag position defined
		// and this will create first render request given these points.
		if (mCurlState == CURL_NONE) {
			return false;
		}

		return true;
	}

	private void completeCurl() {
		RectF rightRect = mRenderer.getPageRect(CurlRenderer.PAGE_RIGHT);
		RectF leftRect = mRenderer.getPageRect(CurlRenderer.PAGE_LEFT);
		if (mCurlState == CURL_LEFT || mCurlState == CURL_RIGHT) {
			// Animation source is the point from where animation starts.
			// Also it's handled in a way we actually simulate touch events
			// meaning the output is exactly the same as if user drags the
			// page to other side. While not producing the best looking
			// result (which is easier done by altering curl position and/or
			// direction directly), this is done in a hope it made code a
			// bit more readable and easier to maintain.
			mAnimationSource.set(mPointerPos.mPos);
			mAnimationStartTime = System.currentTimeMillis();

			// Given the explanation, here we decide whether to simulate
			// drag to left or right end.
			if ((mViewMode == SHOW_ONE_PAGE && mPointerPos.mPos.x > (rightRect.left + rightRect.right) / 2) || mViewMode == SHOW_TWO_PAGES && mPointerPos.mPos.x > rightRect.left) {
				// On right side target is always right page's right border.
				mAnimationTarget.set(mDragStartPos);
				mAnimationTarget.x = mRenderer.getPageRect(CurlRenderer.PAGE_RIGHT).right;
				mAnimationTargetEvent = SET_CURL_TO_RIGHT;

			} else {
				// On left side target depends on visible pages.
				mAnimationTarget.set(mDragStartPos);
				if (mCurlState == CURL_RIGHT || mViewMode == SHOW_TWO_PAGES) {
					mAnimationTarget.x = leftRect.left;
				} else {
					mAnimationTarget.x = rightRect.left;
				}
				mAnimationTargetEvent = SET_CURL_TO_LEFT;

			}
			mAnimate = true;
			requestRender();

		}
	}

	/**
	 * Allow the last page to curl.
	 */
	public void setAllowLastPageCurl(boolean allowLastPageCurl) {
		mAllowLastPageCurl = allowLastPageCurl;
	}

	/**
	 * Sets background color - or OpenGL clear color to be more precise. Color
	 * is a 32bit value consisting of 0xAARRGGBB and is extracted using
	 * android.graphics.Color eventually.
	 */
	@Override
	public void setBackgroundColor(int color) {
		mRenderer.setBackgroundColor(color);
		requestRender();
	}

	/**
	 * Sets mPageCurl curl position.
	 */
	private void setCurlPos(PointF curlPos, PointF curlDir, double radius) {

		// First reposition curl so that page doesn't 'rip off' from book.
		if (mCurlState == CURL_RIGHT || (mCurlState == CURL_LEFT && mViewMode == SHOW_ONE_PAGE)) {
			RectF pageRect = mRenderer.getPageRect(CurlRenderer.PAGE_RIGHT);
			if (curlPos.x >= pageRect.right) {
				mPageCurl.reset();
				requestRender();
				return;
			}
			if (curlPos.x < pageRect.left) {
				curlPos.x = pageRect.left;
			}
			if (curlDir.y != 0) {
				float diffX = curlPos.x - pageRect.left;
				float leftY = curlPos.y + (diffX * curlDir.x / curlDir.y);
				if (curlDir.y < 0 && leftY < pageRect.top) {
					curlDir.x = curlPos.y - pageRect.top;
					curlDir.y = pageRect.left - curlPos.x;
				} else if (curlDir.y > 0 && leftY > pageRect.bottom) {
					curlDir.x = pageRect.bottom - curlPos.y;
					curlDir.y = curlPos.x - pageRect.left;
				}
			}
		} else if (mCurlState == CURL_LEFT) {
			RectF pageRect = mRenderer.getPageRect(CurlRenderer.PAGE_LEFT);
			if (curlPos.x <= pageRect.left) {
				mPageCurl.reset();
				requestRender();
				return;
			}
			if (curlPos.x > pageRect.right) {
				curlPos.x = pageRect.right;
			}
			if (curlDir.y != 0) {
				float diffX = curlPos.x - pageRect.right;
				float rightY = curlPos.y + (diffX * curlDir.x / curlDir.y);
				if (curlDir.y < 0 && rightY < pageRect.top) {
					curlDir.x = pageRect.top - curlPos.y;
					curlDir.y = curlPos.x - pageRect.right;
				} else if (curlDir.y > 0 && rightY > pageRect.bottom) {
					curlDir.x = curlPos.y - pageRect.bottom;
					curlDir.y = pageRect.right - curlPos.x;
				}
			}
		}

		// Finally normalize direction vector and do rendering.
		double dist = Math.sqrt(curlDir.x * curlDir.x + curlDir.y * curlDir.y);
		if (dist != 0) {
			curlDir.x /= dist;
			curlDir.y /= dist;
			mPageCurl.curl(curlPos, curlDir, radius);
		} else {
			mPageCurl.reset();
		}

		requestRender();
	}

	/**
	 * Set current page index. Page indices are zero based values presenting
	 * page being shown on right side of the book. E.g if you set value to 4;
	 * right side front facing bitmap will be with index 4, back facing 5 and
	 * for left side page index 3 is front facing, and index 2 back facing (once
	 * page is on left side it's flipped over).
	 * 
	 * Current index is rounded to closest value divisible with 2.
	 */
	public void setCurrentIndex(int index) {
		if (mPageProvider == null || index < 0) {
			mCurrentIndex = 0;
		} else {
			if (mAllowLastPageCurl) {
				mCurrentIndex = Math.min(index, mPageProvider.getPageCount());
			} else {
				mCurrentIndex = Math.min(index, mPageProvider.getPageCount() - 1);
			}
		}
		updatePages();
		requestRender();

	}

	public interface OnPageChangeListener {
		void pageChanged(int curPage);
	}

	public void setOnPageChangeListener(OnPageChangeListener listener) {
		pageChangeListener = listener;
	}

	/**
	 * If set to true, touch event pressure information is used to adjust curl
	 * radius. The more you press, the flatter the curl becomes. This is
	 * somewhat experimental and results may vary significantly between devices.
	 * On emulator pressure information seems to be flat 1.0f which is maximum
	 * value and therefore not very much of use.
	 */
	public void setEnableTouchPressure(boolean enableTouchPressure) {
		mEnableTouchPressure = enableTouchPressure;
	}

	/**
	 * Set margins (or padding). Note: margins are proportional. Meaning a value
	 * of .1f will produce a 10% margin.
	 */
	public void setMargins(float left, float top, float right, float bottom) {
		mRenderer.setMargins(left, top, right, bottom);
	}

	/**
	 * Update/set page provider.
	 */
	public void setPageProvider(PageProvider pageProvider) {
		mPageProvider = pageProvider;
		mCurrentIndex = 0;
		updatePages();
		requestRender();
	}

	public PageProvider getPageProvider() {
		return mPageProvider;
	}

	/**
	 * Setter for whether left side page is rendered. This is useful mostly for
	 * situations where right (main) page is aligned to left side of screen and
	 * left page is not visible anyway.
	 */
	public void setRenderLeftPage(boolean renderLeftPage) {
		mRenderLeftPage = renderLeftPage;
	}

	/**
	 * Sets SizeChangedObserver for this View. Call back method is called from
	 * this View's onSizeChanged method.
	 */
	public void setSizeChangedObserver(SizeChangedObserver observer) {
		mSizeChangedObserver = observer;
	}

	/**
	 * Sets view mode. Value can be either SHOW_ONE_PAGE or SHOW_TWO_PAGES. In
	 * former case right page is made size of display, and in latter case two
	 * pages are laid on visible area.
	 */
	public void setViewMode(int viewMode) {
		switch (viewMode) {
		case SHOW_ONE_PAGE:
			mViewMode = viewMode;
			mPageLeft.setFlipTexture(true);
			mRenderer.setViewMode(CurlRenderer.SHOW_ONE_PAGE);
			break;
		case SHOW_TWO_PAGES:
			mViewMode = viewMode;
			mPageLeft.setFlipTexture(false);
			mRenderer.setViewMode(CurlRenderer.SHOW_TWO_PAGES);
			break;
		}
	}

	/**
	 * Switches meshes and loads new bitmaps if available. Updated to support 2
	 * pages in landscape
	 */
	private void startCurl(int page) {
		switch (page) {

		// Once right side page is curled, first right page is assigned into
		// curled page. And if there are more bitmaps available new bitmap is
		// loaded into right side mesh.
		case CURL_RIGHT: {
			// Remove meshes from renderer.
			mRenderer.removeCurlMesh(mPageLeft);
			mRenderer.removeCurlMesh(mPageRight);
			mRenderer.removeCurlMesh(mPageCurl);

			// We are curling right page.
			CurlMesh curl = mPageRight;
			mPageRight = mPageCurl;
			mPageCurl = curl;

			if (mCurrentIndex > 0) {
				mPageLeft.setFlipTexture(true);
				mPageLeft.setRect(mRenderer.getPageRect(CurlRenderer.PAGE_LEFT));
				mPageLeft.reset();
				if (mRenderLeftPage) {
					mRenderer.addCurlMesh(mPageLeft);
				}
			}
			if (mCurrentIndex < mPageProvider.getPageCount() - 1) {
				updatePage(mPageRight.getTexturePage(), mCurrentIndex + 1);

				mPageRight.setRect(mRenderer.getPageRect(CurlRenderer.PAGE_RIGHT));
				mPageRight.setFlipTexture(false);
				mPageRight.reset();
				mRenderer.addCurlMesh(mPageRight);
			}

			// Add curled page to renderer.
			mPageCurl.setRect(mRenderer.getPageRect(CurlRenderer.PAGE_RIGHT));
			mPageCurl.setFlipTexture(false);
			mPageCurl.reset();
			mRenderer.addCurlMesh(mPageCurl);

			mCurlState = CURL_RIGHT;
			break;
		}

		// On left side curl, left page is assigned to curled page. And if
		// there are more bitmaps available before currentIndex, new bitmap
		// is loaded into left page.
		case CURL_LEFT: {
			// Remove meshes from renderer.
			mRenderer.removeCurlMesh(mPageLeft);
			mRenderer.removeCurlMesh(mPageRight);
			mRenderer.removeCurlMesh(mPageCurl);

			// We are curling left page.
			CurlMesh curl = mPageLeft;
			mPageLeft = mPageCurl;
			mPageCurl = curl;

			if (mCurrentIndex > 1) {
				updatePage(mPageLeft.getTexturePage(), mCurrentIndex - 2);
				mPageLeft.setFlipTexture(true);
				mPageLeft.setRect(mRenderer.getPageRect(CurlRenderer.PAGE_LEFT));
				mPageLeft.reset();
				if (mRenderLeftPage) {
					mRenderer.addCurlMesh(mPageLeft);
				}
			}

			// If there is something to show on right page add it to renderer.
			if (mCurrentIndex < mPageProvider.getPageCount()) {
				mPageRight.setFlipTexture(false);
				mPageRight.setRect(mRenderer.getPageRect(CurlRenderer.PAGE_RIGHT));
				mPageRight.reset();
				mRenderer.addCurlMesh(mPageRight);
			}

			// How dragging previous page happens depends on view mode.
			if (mViewMode == SHOW_ONE_PAGE || (mCurlState == CURL_LEFT && mViewMode == SHOW_TWO_PAGES)) {
				mPageCurl.setRect(mRenderer.getPageRect(CurlRenderer.PAGE_RIGHT));
				mPageCurl.setFlipTexture(false);
			} else {
				mPageCurl.setRect(mRenderer.getPageRect(CurlRenderer.PAGE_LEFT));
				mPageCurl.setFlipTexture(true);
			}
			mPageCurl.reset();
			mRenderer.addCurlMesh(mPageCurl);

			mCurlState = CURL_LEFT;
			break;
		}

		}

		pageChangeListener.pageChanged(mCurrentIndex);
	}

	/**
	 * Updates curl position.
	 */
	private void updateCurlPos(PointerPosition pointerPos) {

		// Default curl radius.
		double radius = mRenderer.getPageRect(CURL_RIGHT).width() / 3;
		// TODO: This is not an optimal solution. Based on feedback received so
		// far; pressure is not very accurate, it may be better not to map
		// coefficient to range [0f, 1f] but something like [.2f, 1f] instead.
		// Leaving it as is until get my hands on a real device. On emulator
		// this doesn't work anyway.
		radius *= Math.max(1f - pointerPos.mPressure, 0f);
		// NOTE: Here we set pointerPos to mCurlPos. It might be a bit confusing
		// later to see e.g "mCurlPos.x - mDragStartPos.x" used. But it's
		// actually pointerPos we are doing calculations against. Why? Simply to
		// optimize code a bit with the cost of making it unreadable. Otherwise
		// we had to this in both of the next if-else branches.
		mCurlPos.set(pointerPos.mPos);

		// If curl happens on right page, or on left page on two page mode,
		// we'll calculate curl position from pointerPos.
		if (mCurlState == CURL_RIGHT || (mCurlState == CURL_LEFT && mViewMode == SHOW_TWO_PAGES)) {

			mCurlDir.x = mCurlPos.x - mDragStartPos.x;
			mCurlDir.y = mCurlPos.y - mDragStartPos.y;
			float dist = (float) Math.sqrt(mCurlDir.x * mCurlDir.x + mCurlDir.y * mCurlDir.y);

			// Adjust curl radius so that if page is dragged far enough on
			// opposite side, radius gets closer to zero.
			float pageWidth = mRenderer.getPageRect(CurlRenderer.PAGE_RIGHT).width();
			double curlLen = radius * Math.PI;
			if (dist > (pageWidth * 2) - curlLen) {
				curlLen = Math.max((pageWidth * 2) - dist, 0f);
				radius = curlLen / Math.PI;
			}

			// Actual curl position calculation.
			if (dist >= curlLen) {
				double translate = (dist - curlLen) / 2;
				if (mViewMode == SHOW_TWO_PAGES) {
					mCurlPos.x -= mCurlDir.x * translate / dist;
				} else {
					float pageLeftX = mRenderer.getPageRect(CurlRenderer.PAGE_RIGHT).left;
					radius = Math.max(Math.min(mCurlPos.x - pageLeftX, radius), 0f);
				}
				mCurlPos.y -= mCurlDir.y * translate / dist;
			} else {
				double angle = Math.PI * Math.sqrt(dist / curlLen);
				double translate = radius * Math.sin(angle);
				mCurlPos.x += mCurlDir.x * translate / dist;
				mCurlPos.y += mCurlDir.y * translate / dist;
			}
		}
		// Otherwise we'll let curl follow pointer position.
		else if (mCurlState == CURL_LEFT) {

			// Adjust radius regarding how close to page edge we are.
			float pageLeftX = mRenderer.getPageRect(CurlRenderer.PAGE_RIGHT).left;
			radius = Math.max(Math.min(mCurlPos.x - pageLeftX, radius), 0f);

			float pageRightX = mRenderer.getPageRect(CurlRenderer.PAGE_RIGHT).right;
			mCurlPos.x -= Math.min(pageRightX - mCurlPos.x, radius);
			mCurlDir.x = mCurlPos.x + mDragStartPos.x;
			mCurlDir.y = mCurlPos.y - mDragStartPos.y;
		}

		setCurlPos(mCurlPos, mCurlDir, radius);
	}

	/**
	 * Updates given CurlPage via PageProvider for page located at index.
	 */
	private void updatePage(CurlPage page, int index) {
		// First reset page to initial state.
		page.reset();
		// Ask page provider to fill it up with bitmaps and colors.
		mPageProvider.updatePage(page, mPageBitmapWidth, mPageBitmapHeight, index);

	}

	/**
	 * Updates bitmaps for page meshes.
	 */
	private void updatePages() {
		if (mPageProvider == null || mPageBitmapWidth <= 0 || mPageBitmapHeight <= 0) {
			return;
		}

		// Remove meshes from renderer.
		mRenderer.removeCurlMesh(mPageLeft);
		mRenderer.removeCurlMesh(mPageRight);
		mRenderer.removeCurlMesh(mPageCurl);

		int leftIdx = mCurrentIndex - 1;
		int rightIdx = mCurrentIndex;
		int curlIdx = -1;
		if (mCurlState == CURL_LEFT) {
			curlIdx = leftIdx;
			--leftIdx;
		} else if (mCurlState == CURL_RIGHT) {
			curlIdx = rightIdx;
			++rightIdx;
		}

		if (rightIdx >= 0 && rightIdx < mPageProvider.getPageCount()) {
			updatePage(mPageRight.getTexturePage(), rightIdx);
			mPageRight.setFlipTexture(false);
			mPageRight.setRect(mRenderer.getPageRect(CurlRenderer.PAGE_RIGHT));
			mPageRight.reset();
			mRenderer.addCurlMesh(mPageRight);
		}
		if (leftIdx >= 0 && leftIdx < mPageProvider.getPageCount()) {
			updatePage(mPageLeft.getTexturePage(), leftIdx);
			mPageLeft.setFlipTexture(true);
			mPageLeft.setRect(mRenderer.getPageRect(CurlRenderer.PAGE_LEFT));
			mPageLeft.reset();
			if (mRenderLeftPage) {
				mRenderer.addCurlMesh(mPageLeft);
			}
		}
		if (curlIdx >= 0 && curlIdx < mPageProvider.getPageCount()) {
			updatePage(mPageCurl.getTexturePage(), curlIdx);

			if (mCurlState == CURL_RIGHT) {
				mPageCurl.setFlipTexture(true);
				mPageCurl.setRect(mRenderer.getPageRect(CurlRenderer.PAGE_RIGHT));
			} else {
				mPageCurl.setFlipTexture(false);
				mPageCurl.setRect(mRenderer.getPageRect(CurlRenderer.PAGE_LEFT));
			}

			mPageCurl.reset();
			mRenderer.addCurlMesh(mPageCurl);
		}

		pageChangeListener.pageChanged(mCurrentIndex);
	}

	/**
	 * Provider for feeding 'book' with bitmaps which are used for rendering
	 * pages.
	 */
	public interface PageProvider {

		/**
		 * Return phone of pages available.
		 */
		public int getPageCount();

		/**
		 * Called once new bitmaps/textures are needed. Width and height are in
		 * pixels telling the size it will be drawn on screen and following them
		 * ensures that aspect ratio remains. But it's possible to return bitmap
		 * of any size though. You should use provided CurlPage for storing page
		 * information for requested page phone.<br/>
		 * <br/>
		 * Index is a phone between 0 and getBitmapCount() - 1.
		 */
		public void updatePage(CurlPage page, int width, int height, int index);
	}

	/**
	 * Simple holder for pointer position.
	 */
	private class PointerPosition {
		PointF mPos = new PointF();
		float mPressure;
	}

	/**
	 * Observer interface for handling CurlView size changes.
	 */
	public interface SizeChangedObserver {

		/**
		 * Called once CurlView size changes.
		 */
		public void onSizeChanged(int width, int height);
	}

	public void nextPage() {

		int x = getWidth();
		int y = getHeight() / 2;

		MotionEvent me = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, x, y, 0);
		onTouchEvent(me);
		me.recycle();
		for (int i = getWidth(); i > (getWidth() / 2) - 100; i -= 4) {
			x = i;
			me = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_MOVE, x, y, 0);
			onTouchEvent(me);
			me.recycle();
		}
		me = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, x, y, 0);
		onTouchEvent(me);

		me.recycle();
	}

	public void prevPage() {
		int x = 0;
		int y = getHeight() / 2;

		MotionEvent me = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, x, y, 0);
		onTouchEvent(me);
		me.recycle();
		for (int i = 0; i < getWidth() + 100; i += 4) {
			x = i;
			me = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_MOVE, x, y, 0);
			onTouchEvent(me);
			me.recycle();
		}
		me = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, x, y, 0);
		onTouchEvent(me);

		me.recycle();
	}

	

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (!mAnimate && mPageProvider != null) {
			mPointerPos.mPos.set(event.getX(), event.getY());
			mRenderer.translate(mPointerPos.mPos);
			if (mEnableTouchPressure) {
				mPointerPos.mPressure = event.getPressure();
			} else {
				mPointerPos.mPressure = 0.8f;
			}

		}

		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			start.set(event.getX(), event.getY());

			mode = DRAG;

			if (!mAnimate && mPageProvider != null && panZoomManager.zoom == 1f) {

				touchDown();
			}

			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			oldDist = distance(event);

			if (oldDist > 20f) {
				getCenter(mid, event);
				mode = ZOOM;

			}

			

			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			mode = NONE;

			completeCurl();
			
			if(panZoomManager.zoom < 1f){
				panZoomManager.reset();
			}
			

			

			break;
		case MotionEvent.ACTION_MOVE:

			if (mode == DRAG) {

				if (panZoomManager.zoom == 1f) {
					if (!mAnimate && mPageProvider != null)
						updateCurlPos(mPointerPos);
				} else {
					panZoomManager.performPan(event.getX() - start.x, event.getY() - start.y);
					start.set(event.getX(), event.getY());
				}

			} else if (mode == ZOOM) {
				float newDist = distance(event);

				if (newDist > 20f) {
					float scale = newDist / oldDist;
					oldDist = newDist;
					panZoomManager.performZoom(scale, mid);
				}
			}
			break;
		}
		pageChangeListener.pageChanged(mCurrentIndex);
		return true; 
	}

	public class PanZoomManager {

	
		private PointF pan;
		public float zoom;
		private int anchor;
		private View book;

		PanZoomManager(View book, int anchor) {
			pan = new PointF(0, 0);
			zoom = 1f;
			
			this.book = book;
			this.anchor = anchor;
			panZoomChanged();
		}

		public void performZoom(float scale, PointF zoomCenter) {

			float oldZoom = zoom;

			zoom *= scale;

			zoom = Math.max(.7f, zoom);
			zoom = Math.min(maxZoom, zoom);


			float width = book.getWidth();
			float height = book.getHeight();
			float oldScaledWidth = width * oldZoom;
			float oldScaledHeight = height * oldZoom;
			float newScaledWidth = width * zoom;
			float newScaledHeight = height * zoom;



			float reqXPos = ((oldScaledWidth - width) * 0.5f + zoomCenter.x - pan.x) / oldScaledWidth;
			float reqYPos = ((oldScaledHeight - height) * 0.5f + zoomCenter.y - pan.y) / oldScaledHeight;
			float actualXPos = ((newScaledWidth - width) * 0.5f + zoomCenter.x - pan.x) / newScaledWidth;
			float actualYPos = ((newScaledHeight - height) * 0.5f + zoomCenter.y - pan.y) / newScaledHeight;

			pan.x += (actualXPos - reqXPos) * newScaledWidth;
			pan.y += (actualYPos - reqYPos) * newScaledHeight;
			

			panZoomChanged();
		}

		public void performPan(float panX, float panY) {
			pan.x += panX;
			pan.y += panY;
			panZoomChanged();
		}

		private float getMinimumZoom() {
			return 1f;
		}


		public void reset() {
			zoom = getMinimumZoom();
			pan = new PointF(0f, 0f);
			panZoomChanged();
		}

		public void panZoomChanged() {

			if (zoom <= 1f) {
				pan.x = 0;
				pan.y = 0;
			} else if (anchor == CENTER) {

				float maxPanX = (zoom - 1f) * book.getWidth() * 0.5f;
				float maxPanY = (zoom - 1f) * book.getHeight() * 0.5f;
				pan.x = Math.max(-maxPanX, Math.min(maxPanX, pan.x));
				pan.y = Math.max(-maxPanY, Math.min(maxPanY, pan.y));

			} 
	
	
			CurlRenderer.setZoom(zoom);
			CurlRenderer.setZoomX(pan.x);
			CurlRenderer.setZoomY(pan.y);
			requestRender();
			


	
			

		}
	}

	private float distance(MotionEvent event) {

		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return (float) Math.sqrt(x * x + y * y);
	}


	private void getCenter(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

}
