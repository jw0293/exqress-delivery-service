package com.example.deliveryservice.service;

import com.example.deliveryservice.dto.DeliveryDto;
import com.example.deliveryservice.entity.DeliveryEntity;
import com.example.deliveryservice.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class DeliveryServiceImpl implements DeliveryService {

    private final Environment env;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final DeliveryRepository deliveryRepository;
    // private final DeliveryServiceClient deliveryServiceClient;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        DeliveryEntity user =  deliveryRepository.findByEmail(username);

        if(user == null)
            throw new UsernameNotFoundException(username);

        return new User(user.getEmail(), user.getEncryptedPwd(),
                true, true, true, true,
                new ArrayList<>());
    }

    @Override
    public DeliveryDto createUser(DeliveryDto userDto) {
        userDto.setDeliveryId(UUID.randomUUID().toString());

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        DeliveryEntity userEntity = mapper.map(userDto, DeliveryEntity.class);
        userEntity.setEncryptedPwd(bCryptPasswordEncoder.encode(userDto.getPwd()));

        deliveryRepository.save(userEntity);

        DeliveryDto returnDeliveryDto = mapper.map(userEntity, DeliveryDto.class);

        return returnDeliveryDto;
    }

    @Override
    public DeliveryDto getUserDetailsByEmail(String email) {
        DeliveryEntity deliveryEntity = deliveryRepository.findByEmail(email);

        if(deliveryEntity == null){
            throw new IllegalArgumentException();
        }

        return new ModelMapper().map(deliveryEntity, DeliveryDto.class);
    }

    @Override
    public DeliveryDto getUserByUserId(String userId) {
        DeliveryEntity userEntity = deliveryRepository.findByDeliveryId(userId);

        if(userEntity == null){
            throw new UsernameNotFoundException("User not found!");
        }

        DeliveryDto userDto = new ModelMapper().map(userEntity, DeliveryDto.class);

        /** Using RestTemplate **/
//        String deliveryUrl = String.format(env.getProperty("order_service.url"), userId);
//        ResponseEntity<List<ResponseItem>> itemListResponse =
//                restTemplate.exchange(deliveryUrl, HttpMethod.GET, null,
//                            new ParameterizedTypeReference<List<ResponseItem>>() {
//                });

        /** Using a feign client **/


//        List<ResponseItem> itemList = deliveryServiceClient.getItems(userId);
//        userDto.setItems(itemList);

        return userDto;
    }

    @Override
    public boolean isDuplicatedUser(String email) {
        DeliveryEntity deliveryEntity = deliveryRepository.findByEmail(email);
        if(deliveryEntity == null) {
            return false;
        }
        return true;
    }
}
