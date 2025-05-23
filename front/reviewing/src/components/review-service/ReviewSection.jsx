import React, { useEffect, useState } from 'react';
import ReviewModal from './ReviewModal';
import ReviewCard from './ReviewCard';
import styles from './section.module.scss';
import axiosInstance from '../../configs/axios-config';
import axios from 'axios';
import { API_BASE_URL, REVIEW_SERVICE } from '../../configs/host-config';

const ReviewSection = ({
  restaurantId = null,
  userId = null,
  restaurantName,
}) => {
  const [isShownModal, setIsShownModal] = useState(false);
  const [reviews, setReviews] = useState([]);

  const USER_ID = localStorage.getItem('USER_ID');

  useEffect(() => {
    if (restaurantId) {
      fetchReviewsByRestaurant();
    } else if (userId) {
      fetchReviewsByUserId();
    }
  }, []);

  const fetchReviewsByRestaurant = async () => {
    const res = await axios.get(
      `${API_BASE_URL}${REVIEW_SERVICE}/reviews/restaurant/${restaurantId}`,
    );
    console.log(res.data.result);
    setReviews(res.data.result);
  };

  const fetchReviewsByUserId = async () => {
    const res = await axios.get(
      `${API_BASE_URL}${REVIEW_SERVICE}/reviews/user/${userId}`,
    );
    console.log(res.data.result);
    setReviews(res.data.result);
  };

  const handleReviewBtnClick = () => {
    setIsShownModal(true);
  };
  const handleCancelBtnClick = () => {
    setIsShownModal(false);
  };

  return (
    <>
      {isShownModal && (
        <ReviewModal
          handleCancelBtnClick={handleCancelBtnClick}
          onReviewSubmitted={fetchReviewsByRestaurant}
          restaurantName={restaurantName}
        />
      )}

      <div className={styles.entireWrap}>
        {restaurantId && USER_ID && (
          <div className={styles.reviewWriteBtnWrap}>
            <button type='button' onClick={handleReviewBtnClick}>
              리뷰하기
            </button>
          </div>
        )}
        <div className={styles.reviewsWrap}>
          <ul>
            {reviews.map((review) => (
              <li>
                <ReviewCard
                  key={review.id}
                  reviewInfo={review}
                  onReviewSubmitted={fetchReviewsByRestaurant}
                  restaurantName={restaurantName}
                />
              </li>
            ))}
          </ul>
        </div>
      </div>
    </>
  );
};

export default ReviewSection;
