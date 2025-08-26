package futbol.rozbrajacz.rozutils
/*
	Copyright (c) rozbrajaczpoziomow

	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU Affero General Public License version 3
	as published by the Free Software Foundation.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU Affero General Public License for more details.

	You should have received a copy of the GNU Affero General Public License
	along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

/**
 * A cached config reader class
 * @param configValue A supplier of a config value that can change
 * @param parser A supplier of a parser for said config value
 * @property value The parsed latest config value
 * @author rozbrajaczpoziomow
 */
class ConfigHelper<CONFIG, RET>(private val configValue: () -> CONFIG, private val parser: (CONFIG) -> RET) {
	private var currValue: CONFIG = configValue()
	private var parsedValue: RET = parser(currValue)

	val value: RET
		get() {
			val curr = configValue()
			if(curr == currValue)
				return parsedValue

			currValue = curr
			parsedValue = parser(curr)
			return parsedValue
		}
}
