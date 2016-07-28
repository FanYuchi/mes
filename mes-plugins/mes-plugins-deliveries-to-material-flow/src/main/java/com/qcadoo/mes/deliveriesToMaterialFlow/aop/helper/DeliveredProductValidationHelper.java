/**
 * ***************************************************************************
 * Copyright (c) 2010 Qcadoo Limited
 * Project: Qcadoo MES
 * Version: 1.4
 *
 * This file is part of Qcadoo.
 *
 * Qcadoo is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * ***************************************************************************
 */
package com.qcadoo.mes.deliveriesToMaterialFlow.aop.helper;

import static com.qcadoo.mes.deliveries.constants.DeliveredProductFields.DELIVERY;
import static com.qcadoo.mes.deliveries.constants.DeliveredProductFields.PALLET_NUMBER;
import static com.qcadoo.mes.deliveries.constants.DeliveredProductFields.PRODUCT;
import static com.qcadoo.mes.deliveriesToMaterialFlow.constants.DeliveredProductFieldsDTMF.EXPIRATION_DATE;

import org.springframework.stereotype.Service;

import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.search.SearchCriteriaBuilder;
import com.qcadoo.model.api.search.SearchRestrictions;

@Service
public class DeliveredProductValidationHelper {

    public boolean checkDeliveredProductUniqueness(final DataDefinition deliveredProductDD, final Entity deliveredProduct) {
        SearchCriteriaBuilder scb = deliveredProductDD.find()
                .add(SearchRestrictions.belongsTo(DELIVERY, deliveredProduct.getBelongsToField(DELIVERY)))
                .add(SearchRestrictions.belongsTo(PRODUCT, deliveredProduct.getBelongsToField(PRODUCT)))
                .add(SearchRestrictions.belongsTo(PALLET_NUMBER, deliveredProduct.getBelongsToField(PALLET_NUMBER)))
                .add(SearchRestrictions.eq(EXPIRATION_DATE, deliveredProduct.getField(EXPIRATION_DATE)));
        Long deliveredProductId = deliveredProduct.getId();
        if (deliveredProductId != null) {
            scb.add(SearchRestrictions.idNe(deliveredProductId));
        }
        Entity result = scb.setMaxResults(1).uniqueResult();
        if (result != null) {
            deliveredProduct.addError(deliveredProductDD.getField(PRODUCT),
                    "deliveries.deliveredProduct.error.productAlreadyExists");
            return false;
        } else {
            return true;
        }
    }
}