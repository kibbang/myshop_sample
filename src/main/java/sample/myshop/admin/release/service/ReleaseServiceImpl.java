package sample.myshop.admin.release.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sample.myshop.admin.release.repository.ReleaseRepository;
import sample.myshop.release.domain.OrderRelease;
import sample.myshop.release.enums.ReleaseStatus;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReleaseServiceImpl implements ReleaseService {

    private final ReleaseRepository releaseRepository;

    @Override
    @Transactional
    public void toRelease(Long orderId) {

        OrderRelease targetRelease = getOrderRelease(orderId);

        targetRelease.toRelease();
    }

    private OrderRelease getOrderRelease(Long orderId) {
        return releaseRepository.findByOrderId(orderId);
    }
}
